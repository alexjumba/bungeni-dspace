package org.un.undesa.bungeni.crosswalk.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dspace.app.webui.util.JSPManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.search.DSQuery;
import org.dspace.search.QueryArgs;
import org.dspace.search.QueryResults;
import org.dspace.sort.SortOption;

import com.thoughtworks.xstream.XStream;

public class ListCollections extends HttpServlet {
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("\n\nreached showCollections");
		// This will map community IDs to arrays of collections
        Map colMap = new HashMap();

        // This will map communityIDs to arrays of sub-communities
        //Map commMap = new HashMap();

        Community[] communities = null;
        
        
        List<CommunityCollection> ccl = null;
        Context ctx = null;
        try {
			ctx = getContext(request);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
				try {
		        	System.out.println("\n\nin findall try");
		        	communities = Community.findAllTop(ctx);
		        	System.out.println("\n\nin findall try after");
		        }catch(Exception ex) {
		        	System.out.println("\n\nin findall catch");
		        	ex.printStackTrace();
		        }
		        ccl=collect(communities);
		        List<CommunityCollection> ccl2 = new ArrayList<CommunityCollection>();
		        CommunityCollection cn1 = new CommunityCollection();
		        cn1.setCommunityName("Categorized");
		        cn1.setCommunityHandle("Categorized");
		        cn1.setCommunity(ccl);
		        
		        CommunityCollection  cn2 = new CommunityCollection();
		        cn2.setCommunity(alternate(ctx));
		        cn2.setCommunityName("Chronological");
		        cn2.setCommunityHandle("@Chronological");
		        ccl2.add(cn1);
		        ccl2.add(cn2);
		        ccl = ccl2;
				////ccl = (List<CommunityCollection>) CollectionUtils.union(ccl, alternate(ctx));
						
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
        
        /*try {
        	ccl=collect(communities);
        }catch(Exception ex) {
        	ex.printStackTrace();
        }*/        
        
    	XStream xstream = new XStream();
        String xml = xstream.toXML(ccl);
    	
        //System.out.println("\n\n\nExtractRecord doGet ->"+xml);
        
        byte b[]= xml.getBytes();
        
        ByteArrayInputStream bif= new ByteArrayInputStream(b/*2*/);
        OutputStream stream=response.getOutputStream();
        response.setContentType("text/xml");
        
        int nextChar;
        while ( ( nextChar = bif.read() ) != -1  ) {
        	//System.out.println("\n\n\nxml-w1");
        	stream.write(nextChar);
        	//stream.write( '\n' );
        	stream.flush();
        }
        if(bif!=null)bif.close();
        if(stream!=null) stream.close();
	}
	
	private Context getContext(HttpServletRequest request) throws Exception{
        //Context context = null;

        // set all incoming encoding to UTF-8
        request.setCharacterEncoding("UTF-8");
        
        Context c = (Context) request.getAttribute("dspace.context");        

        if (c == null)
        {
            // No context for this request yet
            c = new Context();
            HttpSession session = request.getSession();

            // See if a user has authentication
            Integer userID = (Integer) session.getAttribute(
                    "dspace.current.user.id");
            // Set the session ID and IP address
            c.setExtraLogInfo("session_id=" + request.getSession().getId() + ":ip_addr=" + request.getRemoteAddr());

            // Store the context in the request
            request.setAttribute("dspace.context", c);
        }

        return c;
	}
	
	protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
		
    	doGet(request, response);
    }
	
	private List<CommunityCollection> collect(Community[] communities) throws Exception {
		List<CommunityCollection> ccl = new ArrayList<CommunityCollection>();        
        for(Community cm : communities) {
        	//comm.add(cm);
        	CommunityCollection cc = new CommunityCollection();
        	cc.setCommunityHandle(cm.getHandle());
        	cc.setCommunityName(cm.getMetadata("name"));
        	List<CommunityCollection> lcom = new ArrayList<CommunityCollection>();
		List<CollectionCollection> zcol = new ArrayList<CollectionCollection>();
		if(cm.getSubcommunities().length == 0){
			//System.out.println("\n\n\n\nNo subcommunities");
			List<CollectionCollection> lcol = new ArrayList<CollectionCollection>();
			for(org.dspace.content.Collection cn2: cm.getCollections()) {
				CollectionCollection col = new CollectionCollection();
				col.setCollectionHandle(cn2.getHandle());
				col.setCollectionName(cn2.getMetadata("name"));
				lcol.add(col);
	    	    	}
			//zcol.add(lcol);
			zcol=lcol;
		}else{

			for(org.dspace.content.Community cn: cm.getSubcommunities()) {
				CommunityCollection collect = new CommunityCollection();
				collect.setCommunityHandle(cn.getHandle());
				collect.setCommunityName(cn.getMetadata("name"));
				
				List<CollectionCollection> lcol = new ArrayList<CollectionCollection>();
				for(org.dspace.content.Collection cn2: cn.getCollections()) {
					CollectionCollection col = new CollectionCollection();
			    		col.setCollectionHandle(cn2.getHandle());
			    		col.setCollectionName(cn2.getMetadata("name"));
			    		lcol.add(col);
	    	    		}
				collect.setCollection(lcol); 
				lcom.add(collect);

			}
        	
        		
	    	}
        	if(!lcom.isEmpty()){
        		//System.out.println("\n\nno community");
        		cc.setCommunity(lcom);}
		if(!zcol.isEmpty()){
			//System.out.println("\n\nco Collection");
			cc.setCollection(zcol);}
        	ccl.add(cc);        	
        }
        return ccl;
	}
	
	private List<CommunityCollection> alternate(Context ctx) throws Exception {
		System.out.println("\nreached alternate");
		List<CommunityCollection> ccll = new ArrayList<CommunityCollection>(); 
		for(int i = 1; i <= 30;i++) {
			//System.out.println("\nreached iloop");
			CommunityCollection ccl = null;
			
			
			List<CollectionCollection> ccll2 = new ArrayList<CollectionCollection>();
			for(int j = 1900; j <= 2100; j++) {				
				
				//System.out.println("\nreached jloop");
				String query = "(govdocument: "+i+" issue_date:["+j+" TO "+j+"])";
				//System.out.println("\n\nquery: "+query);
				QueryResults qrs = null;
				QueryArgs qa = new QueryArgs();
				qa.setQuery(query);
				qa.setPageSize(2);
				qa.setStart(0);
				qa.setSortOrder(SortOption.DESCENDING);
				qrs = DSQuery.doQuery(ctx, qa);
				java.util.List<String> handles = qrs.getHitHandles();
				if(!handles.isEmpty()) {
					ccl = new CommunityCollection();
					
					ccl.setCommunityName(i+"th Parliament");
					ccl.setCommunityHandle(String.valueOf(i));
					
					//System.out.println("\n\nhandles not empty!");
					
					CollectionCollection ccl2 = new CollectionCollection();
					ccl2.setCollectionName(String.valueOf(j));
					ccl2.setCollectionHandle("*"+String.valueOf(j));
					
					ccll2.add(ccl2);
					//ccl.setCollection(ccll2);
					
					//System.out.println("parliament: "+i+" year:"+j);
					//ccll.add(ccl);
					////break;
				}				
			}
			if(!ccll2.isEmpty() && ccl!=null)ccl.setCollection(ccll2);			
			if(ccl!=null)ccll.add(ccl);
		}
		return ccll;
		
	}
}
