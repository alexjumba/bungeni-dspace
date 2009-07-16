/*
 * ItemImport.java
 *
 * Version: $Revision: 2398 $
 *
 * Date: $Date: 2007-11-28 13:07:34 -0800 (Wed, 28 Nov 2007) $
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.app.itemimport;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.xpath.XPathAPI;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.content.Bitstream;
import org.dspace.content.BitstreamFormat;
import org.dspace.content.Bundle;
import org.dspace.content.Collection;
import org.dspace.content.FormatIdentifier;
import org.dspace.content.InstallItem;
import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.content.WorkspaceItem;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.handle.HandleManager;
import org.dspace.workflow.WorkflowManager;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.*;

/**
 * Import items into DSpace. The conventional use is upload files by copying
 * them. DSpace writes the item's bitstreams into its assetstore. Metadata is
 * also loaded to the DSpace database.
 * <P>
 * A second use assumes the bitstream files already exist in a storage
 * resource accessible to DSpace. In this case the bitstreams are 'registered'.
 * That is, the metadata is loaded to the DSpace database and DSpace is given
 * the location of the file which is subsumed into DSpace.
 * <P>
 * The distinction is controlled by the format of lines in the 'contents' file.
 * See comments in processContentsFile() below.
 * <P>
 * Modified by David Little, UCSD Libraries 12/21/04 to
 * allow the registration of files (bitstreams) into DSpace.
 */
public class WebServiceImport extends HttpServlet
{
    static boolean useWorkflow = false;

    static boolean isTest = false;

    static boolean isResume = false;
    
    static boolean template = false;
    

    //static PrintWriter mapOut = null;

    // File listing filter to look for metadata files
    /*static FilenameFilter metadataFileFilter = new FilenameFilter()
    {
        public boolean accept(File dir, String n)
        {
            return n.startsWith("metadata_");
        }
    };*/

    // File listing filter to check for folders
    /*
    static FilenameFilter directoryFilter = new FilenameFilter()
    {
        public boolean accept(File dir, String n)
        {
            File item = new File(dir.getAbsolutePath() + File.separatorChar + n);
            return item.isDirectory();
        }
    };*/


    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
    	
        
    	doGet(request, response);
    }
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {

    	
        //String command = null; // add replace remove, etc
        String sourcedir = request.getParameter("dublin_core");
        //String mapfile = null;
        String eperson = request.getParameter("eperson"); // db ID or email
        //eperson="alex@parliaments.info";
        String[] collections = new String[1];//{""};//null; // db ID or handles
        //collections[0]=request.getParameter("collection");
        
        List<FileDetails> filelist = new ArrayList<FileDetails>();   
     // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart) {
        	// Create a factory for disk-based file items
        	FileItemFactory factory = new DiskFileItemFactory();

        	// Create a new file upload handler
        	ServletFileUpload upload = new ServletFileUpload(factory);

        	// Parse the request
        	List /* FileItem */ items = null;
        	try {
        		items = upload.parseRequest(request);
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	 
        	// Process the uploaded items
        	Iterator iter = items.iterator();
        	while (iter.hasNext()) {
        	    FileItem item = (FileItem) iter.next();

        	    System.out.println("\n\n\nat while fileitem");
        	    if (item.isFormField()) {
        	        //processFormField(item);
        	    	//if (item.isFormField()) {
        	    	    String name = item.getFieldName();
        	    	    String value = item.getString();
        	    	    if(name.equalsIgnoreCase("collection")) {
        	    	    	collections[0] = value;
        	    	    }else if(name.equalsIgnoreCase("eperson")) {
        	    	    	eperson = value;
        	    	    }
        	    	    if(name.equalsIgnoreCase("dublin_core")) {
        	    	    	sourcedir = value;
        	    	    }
        	    	//}
        	    }else {
        	    	System.out.println("\n\n\nat while fileitem not from field");
        	    	FileDetails fd = new FileDetails();
        	    	fd.setIs(item.getInputStream());
        	    	fd.setName(item.getName());
            	    filelist.add(fd);
            	    for(FileDetails fdd: filelist) {
            	    	System.out.println("\n\nEcho:"+fdd.getName());
            	    }
        	    }
        	}
        }
        
        
        
        
        System.out.println("\n\ncollection is:"+collections[0]+"    "+eperson+"    "+sourcedir);
        int status = 0;   
        
        

        // do checks around mapfile - if mapfile exists and 'add' is selected,
        // resume must be chosen
        //File myFile = new File(mapfile);

        WebServiceImport myloader = new WebServiceImport();

        // create a context
        Context c = null;
        try {
        	c = new Context();
        }catch(Exception e) {
        	e.printStackTrace();
        }

        // find the EPerson, assign to context
        EPerson myEPerson = null;

        
        if (eperson.indexOf('@') != -1)
        {
            // @ sign, must be an email
        	try {
        		myEPerson = EPerson.findByEmail(c, eperson);
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
            
        }
        else
        {
        	try {
        		myEPerson = EPerson.find(c, Integer.parseInt(eperson));
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
            
        }

        if (myEPerson == null)
        {
            System.out.println("Error, eperson cannot be found: " + eperson);
            //System.exit(1);
            return;
        }

        c.setCurrentUser(myEPerson);

        // find collections
        Collection[] mycollections = null;

        // don't need to validate collections set if command is "delete"
        
        //if (!command.equals("delete"))
        //{
            System.out.println("Destination collections:");

            System.out.println("\n\nleng"+collections.length);
            mycollections = new Collection[collections.length];

            // validate each collection arg to see if it's a real collection
            for (int i = 0; i < collections.length; i++)
            {	System.out.println("\n\nlen2:"+collections[0]);
                // is the ID a handle?
                //if (collections[i].indexOf('/') != -1)
                //{
                	System.out.println("\n\nIs a collection handle");
                    // string has a / so it must be a handle - try and resolve
                    // it
                    mycollections[i] = null;
                    try {
                    	mycollections[i] = (Collection) HandleManager
                        .resolveToObject(c, collections[i]);
                    }catch(Exception e) {
                    	e.printStackTrace();
                    }

                    // resolved, now make sure it's a collection
                    if ((mycollections[i] == null)
                            || (mycollections[i].getType() != Constants.COLLECTION))
                    {
                        mycollections[i] = null;
                    }
                //}
                // not a handle, try and treat it as an integer collection
                // database ID
                /*else if (collections[i] != null)
                {
                	System.out.println("\n\nIs a database Id");
                    mycollections[i] = null;
                    try {
                    	mycollections[i]=Collection.find(c, Integer
                                .parseInt(collections[i]));
                    }catch(Exception e) {
                    	e.printStackTrace();
                    }
                }*/

                // was the collection valid?
                if (mycollections[i] == null)
                {
                    throw new IllegalArgumentException("Cannot resolve "
                            + collections[i] + " to collection");
                }

                // print progress info
                String owningPrefix = "";

                if (i == 0)
                {
                    owningPrefix = "Owning ";
                }

                System.out.println("\n\nGotten collection");
                System.out.println(owningPrefix + " Collection: "
                        + mycollections[i].getMetadata("name"));
            }
        //} // end of validating collections

        
        
        try
        {
            c.setIgnoreAuthorization(true);

            /*if (command.equals("add"))
            {*/
                myloader.addItems(c, mycollections, sourcedir/*, mapfile*/, template, request, filelist);
            /*}*/

            // complete all transactions
            c.complete();
        }
        catch (Exception e)
        {
            // abort all operations
            /*if (mapOut != null)
            {
                mapOut.close();
            }

            mapOut = null;*/

            c.abort();
            e.printStackTrace();
            System.out.println(e);
            status = 1;
        }

        /*if (mapOut != null)
        {
            mapOut.close();
        }*/

        if (isTest)
        {
            System.out.println("***End of Test Run***");
        }
        //System.exit(status);
        return;
    }

    private void addItems(Context c, Collection[] mycollections,
            String sourceDir, /*String mapFile,*/ boolean template, HttpServletRequest request, List<FileDetails> filelist) throws Exception
    {
        //Map skipItems = new HashMap(); // set of items to skip if in 'resume'
        // mode

        System.out.println("Adding items from directory: " + sourceDir);
        //System.out.println("Generating mapfile: " + mapFile);

        // create the mapfile
        //File outFile = null;

        if (!isTest)
        {
            // get the directory names of items to skip (will be in keys of
            // hash)
            /*if (isResume)
            {
                skipItems = readMapFile(mapFile);
            }*/

            // sneaky isResume == true means open file in append mode
            /*outFile = new File(mapFile);
            mapOut = new PrintWriter(new FileWriter(outFile, isResume));

            if (mapOut == null)
            {
                throw new Exception("can't open mapfile: " + mapFile);
            }*/
        }

        // open and process the source directory
        /*File d = new java.io.File(sourceDir);

        if (d == null)
        {
            System.out.println("Error, cannot open source directory "
                    + sourceDir);
            System.exit(1);
        }

        String[] dircontents = d.list(directoryFilter);*/

        /*for (int i = 0; i < dircontents.length; i++)
        {
            if (skipItems.containsKey(dircontents[i]))
            {
                System.out.println("Skipping import of " + dircontents[i]);
            }
            else
            {*/
                addItem(c, mycollections, sourceDir, /*dircontents[i]*/"itemname"/*, mapOut*/, template, request, filelist);
                //System.out.println(i + " " + dircontents[i]);
            /*}
        }*/
    }

    /**
     * item? try and add it to the archive c mycollection path itemname handle -
     * non-null means we have a pre-defined handle already mapOut - mapfile
     * we're writing
     */
    private Item addItem(Context c, Collection[] mycollections, String path,
            String itemname, /*PrintWriter mapOut,*/ boolean template, HttpServletRequest request, List<FileDetails> filelist) throws Exception
    {
        //String mapOutput = null;

        System.out.println("Adding item from directory " + itemname);

        // create workspace item
        Item myitem = null;
        WorkspaceItem wi = null;

        if (!isTest)
        {
            wi = WorkspaceItem.create(c, mycollections[0], template);
            myitem = wi.getItem();
        }

        // now fill out dublin core for item
        loadMetadata(c, myitem, path/* + File.separatorChar + itemname
                + File.separatorChar*/);

        // and the bitstreams from the contents file
        // process contents file, add bistreams and bundles, return any
        // non-standard permissions
        /*Vector options = */processContentsFile(c, myitem, path/*
                + File.separatorChar + itemname*/, "contents", request, filelist);

        if (useWorkflow)
        {
            // don't process handle file
            // start up a workflow
            if (!isTest)
            {
                WorkflowManager.startWithoutNotify(c, wi);

                // send ID to the mapfile
                //mapOutput = itemname + " " + myitem.getID();
            }
        }
        else
        {
            // only process handle file if not using workflow system
            String myhandle = processHandleFile(c, myitem, path/*
                    + File.separatorChar + itemname*/, "handle");

            // put item in system
            if (!isTest)
            {
            	System.out.println("\n\nnot test");
                InstallItem.installItem(c, wi, myhandle);

                System.out.println("\n\nnot test2");
                // find the handle, and output to map file
                myhandle = HandleManager.findHandle(c, myitem);
                System.out.println("\n\nnot test2-after");

                //mapOutput = itemname + " " + myhandle;
            }

            // set permissions if specified in contents file
            /*if (options.size() > 0)
            {
                System.out.println("Processing options");
                processOptions(c, myitem, options);
            }*/
        }

        // now add to multiple collections if requested
        if (mycollections.length > 1)
        {
            for (int i = 1; i < mycollections.length; i++)
            {
                if (!isTest)
                {
                	System.out.println("\n\nnot test3");
                    mycollections[i].addItem(myitem);
                }
            }
        }

        // made it this far, everything is fine, commit transaction
        /*if (mapOut != null)
        {
            mapOut.println(mapOutput);
        }*/

        c.commit();

        return myitem;
    }

    ////////////////////////////////////
    // utility methods
    ////////////////////////////////////
    // read in the map file and generate a hashmap of (file,handle) pairs
    private Map readMapFile(String filename) throws Exception
    {
        Map myhash = new HashMap();

        BufferedReader is = null;
        try
        {
            is = new BufferedReader(new FileReader(filename));

            String line;

            while ((line = is.readLine()) != null)
            {
                String myfile;
                String myhandle;

                // a line should be archive filename<whitespace>handle
                StringTokenizer st = new StringTokenizer(line);

                if (st.hasMoreTokens())
                {
                    myfile = st.nextToken();
                }
                else
                {
                    throw new Exception("Bad mapfile line:\n" + line);
                }

                if (st.hasMoreTokens())
                {
                    myhandle = st.nextToken();
                }
                else
                {
                    throw new Exception("Bad mapfile line:\n" + line);
                }

                myhash.put(myfile, myhandle);
            }
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }

        return myhash;
    }

    // Load all metadata schemas into the item.
    private void loadMetadata(Context c, Item myitem, String path)
            throws SQLException, IOException, ParserConfigurationException,
            SAXException, TransformerException, AuthorizeException
    {
        // Load the dublin core metadata
        loadDublinCore(c, myitem, path/* + "dublin_core.xml"*/);

        // Load any additional metadata schemas
        /*File folder = new File(path);
        File file[] = folder.listFiles(metadataFileFilter);
        for (int i = 0; i < file.length; i++)
        {
            loadDublinCore(c, myitem, file[i].getAbsolutePath());
        }*/
    }

    private void loadDublinCore(Context c, Item myitem, String filename)
            throws SQLException, IOException, ParserConfigurationException,
            SAXException, TransformerException, AuthorizeException
    {
        Document document = loadXML(filename);

        // Get the schema, for backward compatibility we will default to the
        // dublin core schema if the schema name is not available in the import
        // file
        String schema;
        NodeList metadata = XPathAPI.selectNodeList(document, "/dublin_core");
        Node schemaAttr = metadata.item(0).getAttributes().getNamedItem(
                "schema");
        if (schemaAttr == null)
        {
            schema = MetadataSchema.DC_SCHEMA;
        }
        else
        {
            schema = schemaAttr.getNodeValue();
        }
         
        // Get the nodes corresponding to formats
        NodeList dcNodes = XPathAPI.selectNodeList(document,
                "/dublin_core/dcvalue");

        System.out.println("\tLoading dublin core from " + filename);

        // Add each one as a new format to the registry
        for (int i = 0; i < dcNodes.getLength(); i++)
        {
            Node n = dcNodes.item(i);
            addDCValue(c, myitem, schema, n);
        }
    }

    private void addDCValue(Context c, Item i, String schema, Node n) throws TransformerException, SQLException, AuthorizeException
    {
        String value = getStringValue(n); //n.getNodeValue();
        // compensate for empty value getting read as "null", which won't display
        if (value == null)
            value = "";
        // //getElementData(n, "element");
        String element = getAttributeValue(n, "element");
        String qualifier = getAttributeValue(n, "qualifier"); //NodeValue();
        // //getElementData(n,
        // "qualifier");
        String language = getAttributeValue(n, "language");

        System.out.println("\tSchema: " + schema + " Element: " + element + " Qualifier: " + qualifier
                + " Value: " + value);

        if (qualifier.equals("none") || "".equals(qualifier))
        {
            qualifier = null;
        }

        // if language isn't set, use the system's default value
        if (language.equals(""))
        {
            language = ConfigurationManager.getProperty("default.language");
        }

        // a goofy default, but there it is
        if (language == null)
        {
            language = "en";
        }

        if (!isTest)
        {
            i.addMetadata(schema, element, qualifier, language, value);
        }
        else
        {
            // If we're just test the import, let's check that the actual metadata field exists.
        	MetadataSchema foundSchema = MetadataSchema.find(c,schema);
        	
        	if (foundSchema == null)
        	{
        		System.out.println("ERROR: schema '"+schema+"' was not found in the registry.");
        		return;
        	}
        	
        	int schemaID = foundSchema.getSchemaID();
        	MetadataField foundField = MetadataField.findByElement(c, schemaID, element, qualifier);
        	
        	if (foundField == null)
        	{
        		System.out.println("ERROR: Metadata field: '"+schema+"."+element+"."+qualifier+"' was not found in the registry.");
        		return;
            }		
        }
    }

    /**
     * Read in the handle file or return null if empty or doesn't exist
     */
    private String processHandleFile(Context c, Item i, String path,
            String filename)
    {
        String filePath = path + File.separatorChar + filename;
        //String line = "";
        String result = null;

        System.out.println("Processing handle file: " + filename);
        BufferedReader is = null;
        try
        {
            is = new BufferedReader(new FileReader(filePath));

            // result gets contents of file, or null
            result = is.readLine();

            System.out.println("read handle: '" + result + "'");

        }
        catch (Exception e)
        {
            // probably no handle file, just return null
            System.out
                    .println("It appears there is no handle file -- generating one");
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e1)
                {
                    System.err
                            .println("Non-critical problem releasing resources.");
                }
            }
        }

        return result;
    }

    /**
     * Given a contents file and an item, stuffing it with bitstreams from the
     * contents file Returns a Vector of Strings with lines from the contents
     * file that request non-default bitstream permission
     */
    private Vector processContentsFile(Context c, Item i, String path,
            String filename, HttpServletRequest request, List<FileDetails> filelist) throws SQLException, IOException,
            AuthorizeException
    {
    	System.out.println("\n\nProcess contents file");
    	// Check that we have a file upload request
        //boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        ////if(isMultipart) {
        	// Create a factory for disk-based file items
        	////FileItemFactory factory = new DiskFileItemFactory();

        	// Create a new file upload handler
        	////ServletFileUpload upload = new ServletFileUpload(factory);

        	// Parse the request
        	//List /* FileItem */ items = null;
        	/*try {
        		items = upload.parseRequest(request);
        	}catch(Exception e) {
        		e.printStackTrace();
        	}*/
        	
        	// Process the uploaded items
        	//Iterator iter = items.iterator();
        	System.out.println("\n\nb4 while it");
        	for(FileDetails item: filelist) {

        		System.out.println("\n\ninside while it");
        	    //FileItem item = (FileItem) iter.next();

        	    //if (item.isFormField()) {
        	        //processFormField(item);
        	    //	System.out.println("\n\nis form field:"+item.getFieldName()+"    "+item.getName());
        	    //} else {
        	        //processUploadedFile(item);
        	    	// Process a file upload
        	    	System.out.println("\n\nnot form field"+item.getName());
        	    	//if (!item.isFormField()) {
        	    	    /*String fieldName = item.getFieldName();
        	    	    String fileName = item.getName();
        	    	    String contentType = item.getContentType();
        	    	    boolean isInMemory = item.isInMemory();
        	    	    long sizeInBytes = item.getSize();*/
        	    	    
        	    	    
        	    	    BufferedInputStream bis = new BufferedInputStream(item.getIs());
        	    	    //String line = "";////
        	    	    processContentFileEntry(c, i, /*path*/bis, /*line*/item.getName(), null);
        	    	//}
        	    //}
        	}
        	//while (iter.hasNext()) {
        	//}
        ////}
        return null;//options;
    }

    /**
     * each entry represents a bitstream....
     * @param c
     * @param i
     * @param path
     * @param fileName
     * @param bundleName
     * @throws SQLException
     * @throws IOException
     * @throws AuthorizeException
     */
    private void processContentFileEntry(Context c, Item i, /*String*/BufferedInputStream /*path*/bis,
            String fileName, String bundleName) throws SQLException,
            IOException, AuthorizeException
    {
    	System.out.println("\n\nprocessContentFileEntry");
        //String fullpath = path + File.separatorChar + fileName;

        // get an input stream
        /*BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                fullpath));*/

        Bitstream bs = null;
        String newBundleName = bundleName;

        if (bundleName == null)
        {
            // is it license.txt?
            if (fileName.equals("license.txt"))
            {
                newBundleName = "LICENSE";
            }
            else
            {
                // call it ORIGINAL
                newBundleName = "ORIGINAL";
            }
        }

        if (!isTest)
        {
            // find the bundle
            Bundle[] bundles = i.getBundles(newBundleName);
            Bundle targetBundle = null;

            if (bundles.length < 1)
            {
                // not found, create a new one
                targetBundle = i.createBundle(newBundleName);
            }
            else
            {
                // put bitstreams into first bundle
                targetBundle = bundles[0];
            }

            // now add the bitstream
            bs = targetBundle.createBitstream(bis);

            bs.setName(fileName);

            // Identify the format
            // FIXME - guessing format guesses license.txt incorrectly as a text
            // file format!
            BitstreamFormat bf = FormatIdentifier.guessFormat(c, bs);
            bs.setFormat(bf);

            bs.update();
        }
    }

    // XML utility methods
    /**
     * Lookup an attribute from a DOM node.
     * @param n
     * @param name
     * @return
     */
    private String getAttributeValue(Node n, String name)
    {
        NamedNodeMap nm = n.getAttributes();

        for (int i = 0; i < nm.getLength(); i++)
        {
            Node node = nm.item(i);

            if (name.equals(node.getNodeName()))
            {
                return node.getNodeValue();
            }
        }

        return "";
    }

    
    /**
     * Return the String value of a Node.
     * @param node
     * @return
     */
    private String getStringValue(Node node)
    {
        String value = node.getNodeValue();

        if (node.hasChildNodes())
        {
            Node first = node.getFirstChild();

            if (first.getNodeType() == Node.TEXT_NODE)
            {
                return first.getNodeValue();
            }
        }

        return value;
    }

    /**
     * Load in the XML from file.
     * 
     * @param filename
     *            the filename to load from
     * 
     * @return the DOM representation of the XML file
     */
    private static Document loadXML(String filename) throws IOException,
            ParserConfigurationException, SAXException
    {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();

        return builder.parse(/*new File(filename)*/new ByteArrayInputStream(filename.getBytes("UTF-8")));
    }
    
    public class FileDetails{
    	private String name;
    	private InputStream is;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public InputStream getIs() {
			return is;
		}
		public void setIs(InputStream is) {
			this.is = is;
		}
    	
    }
}
