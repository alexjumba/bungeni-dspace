Ext.onReady(function(){
	
	    var catalog = [{
        title: 'Combination Samples',
        samples: [{
            text: 'Explorer Demo',
            url: 'http://extjs.com/explorer',
            icon: 'explorer.gif',
            desc: 'Explore the Ext GWT Components and quickly view the source code to see the API in action.'
        },{
            text: 'Mail App',
            url: 'http://extjs.com/mail',
            icon: 'mail.gif',
            desc: 'A mail application with a preview pane that retrieves data using the Ext GWT data loading API.'
        },{
            text: 'Web Desktop',
            url: 'http://extjs.com/deploy/gxt-1.2.1/samples/desktop/www/com.extjs.gxt.samples.desktop.DesktopApp/',
            icon: 'desktop.gif',
            desc: 'Demonstrates how one could build a desktop in the browser using Ext components including a module plugin system.'
        }]
    },{
        title: 'Grids',
        samples: [{
            text: 'Basic Grid',
            url: 'http://extjs.com/examples/grid/grid.html',
            icon: 'basicgrid.gif',
            desc: 'A basic read-only grid loaded from local data that demonstrates the use of custom column renderers.'
        },{
            text: 'Editable Grid',
            url: 'http://extjs.com/examples/grid/editable.html',
            icon: 'editablegrid.gif',
            desc: 'An editable grid loaded from local data that shows multiple types of grid editors..'
        },{
            text: 'XML Grid',
            url: 'http://extjs.com/examples/grid/xml.html',
            icon: 'xmlgrid.gif',
            desc: 'A simple read-only grid loaded from XML data.'
        },{
            text: 'Paging',
            url: 'http://extjs.com/examples/grid/paging.html',
            icon: 'paging.gif',
            desc: 'A grid with server side paging, using GWT RPC.'
        },{
            text: 'Grouping',
            url: 'http://extjs.com/examples/grid/grouping.html',
            icon: 'grouping.gif',
            desc: 'A basic grouping grid showing collapsible data groups that can be customized via the "Group By" header menu option.'
        },{
            text: 'Live Group Summary',
            url: 'http://extjs.com/examples/grid/totals.html',
            icon: 'livegroupsummary.gif',
            desc: 'Advanced grouping grid that allows cell editing and includes custom dynamic summary calculations.'
        },{
            text: 'Grid Plugins',
            url: 'http://extjs.com/examples/grid/plugins.html',
            icon: 'gridplugins.gif',
            desc: 'Multiple grids customized via plugins: expander rows, checkbox selection and row numbering.'
        }]
    },{
        title: 'Tabs',
        samples: [{
            text: 'Basic Tabs',
            url: 'http://extjs.com/examples/tabs/tabs.html',
            icon: 'basictabs.gif',
            desc: 'Basic tab functionality including autoHeight, tabs from markup, Ajax loading and tab events.'
        },{
            text: 'Advanced Tabs',
            url: 'http://extjs.com/examples/tabs/advanced.html',
            icon: 'advancedtabs.gif',
            desc: 'Advanced tab features including tab scrolling, adding tabs programmatically.'
        }]
    },{
        title: 'Drag and Drop',
        samples: [{
            text: 'List to List',
            url: 'http://extjs.com/examples/dnd/listtolist.html',
            icon: 'listtolist.gif',
            desc: 'Drag and drop between two lists.'
        },{
            text: 'Grid to Grid',
            url: 'http://extjs.com/examples/dnd/gridtogrid.html',
            icon: 'gridtogrid.gif',
            desc: 'Drag and drop between two grids supporting both appends and inserts.'
        },{
	        text: 'Tree to Tree',
	        url: 'http://extjs.com/examples/dnd/treetotree.html',
	        icon: 'treetotree.gif',
	        desc: 'Drag and drop between two sorted trees.'
        },{
	        text: 'Reordering Tree',
	        url: 'http://extjs.com/examples/dnd/reorderingtree.html',
	        icon: 'reorderingtree.gif',
	        desc: 'A single tree where nodes and leafs can be reordered.'
        },{
	        text: 'Image Organizer',
	        url: 'http://extjs.com/examples/dnd/imageorganizer.html',
	        icon: 'imageorganizer.gif',
	        desc: 'The image organizer shows an example of dragging a picture from a list to a folder in a tree.'
        }]
    },{
        title: 'Windows',
        samples: [{
            text: 'Hello World',
            url: 'http://extjs.com/examples/window/hello.html',
            icon: 'helloworld.gif',
            desc: 'Simple "Hello World" window that contains a basic TabPanel.'
        },{
            text: 'Accordion Window',
            url: 'http://extjs.com/examples/window/accordion.html',
            icon: 'accordionwindow.gif',
            desc: 'Window with a nested AccordionLayout.'
        },{
            text: 'MessageBox',
            url: 'http://extjs.com/examples/window/messagebox.html',
            icon: 'messagebox.gif',
            desc: 'Different styles include confirm, alert, prompt, progress and wait and also support custom icons.'
        },{
            text: 'Dialog',
            url: 'http://extjs.com/examples/window/dialog.html',
            icon: 'dialog.gif',
            desc: 'Windows with specialized support for buttons.'
        }]
    },{
        title: 'Trees',
        samples: [{
            text: 'Basic Tree',
            url: 'http://extjs.com/examples/tree/basic.html',
            icon: 'basictree.gif',
            desc: 'A basic singl-select tree with custom icons.'
        },{
            text: 'Async Tree',
            url: 'http://extjs.com/examples/tree/async.html',
            icon: 'asynctree.gif',
            desc: 'A tree that loads its children on-demand.'
 		},{
            text: 'Context Menu Tree',
            url: 'http://extjs.com/examples/tree/contextmenu.html',
            icon: 'contextmenutree.gif',
            desc: 'A tree with custom context menu for adding and removing nodes.'
        },{
            text: 'Checkbox Tree',
            url: 'http://extjs.com/examples/tree/checkbox.html',
            icon: 'checkboxtree.gif',
            desc: 'A checkbox tree with cascading check behavior.'
        },{
            text: 'Filter Tree',
            url: 'http://extjs.com/examples/tree/filter.html',
            icon: 'filtertree.gif',
            desc: 'A tree that filters its content based on the input in a store filter text field.'
		},{
            text: 'TreeTable',
            url: 'http://extjs.com/examples/tree/treetable.html',
            icon: 'treetable.gif',
            desc: 'A custom tree with table based column support.'
        }]
    },{
        title: 'Layout Managers',
        samples: [{
            text: 'Border Layout',
            url: 'http://extjs.com/examples/layouts/borderlayout.html',
            icon: 'borderlayout.gif',
            desc: 'A complex BorderLayout implementation that shows nesting multiple components and sub-layouts.'
        },{
            text: 'Accordion Layout',
            url: 'http://extjs.com/examples/layouts/accordionlayout.html',
            icon: 'accordionlayout.gif',
            desc: 'A example of accordioan layout which stacks its chidlren in collapsible panels.'
        },{
            text: 'Anchor Layout',
            url: 'http://extjs.com/examples/layouts/anchorlayout.html',
            icon: 'anchorlayout.gif',
            desc: 'A simple example of anchoring form fields to a window for flexible form resizing.'
		},{
            text: 'Row Layout',
            url: 'http://extjs.com/examples/layouts/rowlayout.html',
            icon: 'rowlayout.gif',
            desc: 'Lays out the components in a single row or column, allowing precise control over sizing.'
        },{
            text: 'CenterLayout',
            url: 'http://extjs.com/examples/layouts/centerlayout.html',
            icon: 'centerlayout.gif',
            desc: 'Centers the child component in its container.'
		},{
            text: 'Portal Demo',
            url: 'http://extjs.com/examples/portal/portal.html',
            icon: 'portal.gif',
            desc: 'A page layout using several custom extensions to provide a web portal interface.'
        }]
    },{
        title: 'ComboBox',
        samples: [{
            text: 'Basic ComboBox',
            url: 'http://extjs.com/examples/forms/combos.html',
            icon: 'combobox.gif',
            desc: 'Basic combos with auto-complete, type ahead, custom templates.'
        },{
            text: 'ComboBox Templates',
            url: 'http://extjs.com/examples/forms/forumsearch.html',
            icon: 'forumsearch.gif',
            desc: 'Customized combo with template-based list rendering, remote loading and paging.'
        }]
    },{
        title: 'Forms',
        samples: [{
            text: 'Forms',
            url: 'http://extjs.com/examples/forms/forms.html',
            icon: 'forms.gif',
            desc: 'Various example forms showing collapsible fieldsets.'
        },{
            text: 'Advanced Forms',
            url: 'http://extjs.com/examples/forms/advanced.html',
            icon: 'advancedforms.gif',
            desc: 'Advanced form layouts with nested column layout and tab panels.'
        },{
            text: 'DualListField',
            url: 'http://extjs.com/examples/forms/duallistfield.html',
            icon: 'duallistfield.gif',
            desc: 'A field that displays two list fields and allows selections to be dragged between lists.'
        },{
            text: 'File Upload',
            url: 'http://extjs.com/examples/forms/fileupload.html',
            icon: 'fileupload.gif',
            desc: 'A field that allows a user to upload a file via a standard HTML submit.'
        }]
    },{
        title: 'Data Binding',
        samples: [{
            text: 'Basic Binding',
            url: 'http://extjs.com/examples/binding/basicbinding.html',
            icon: 'basicbinding.gif',
            desc: 'Basic binding between model and a form.'
        },{
            text: 'Grid Binding',
            url: 'http://extjs.com/examples/binding/gridbinding.html',
            icon: 'gridbinding.gif',
            desc: 'Demonstrates an example of binding a model to a form based on the selection of a grid.'
        },{
            text: 'Grid Store Binding',
            url: 'http://extjs.com/examples/binding/gridstorebinding.html',
            icon: 'gridstorebinding.gif',
            desc: 'Edits are made to the grid are done via the store via records. Edits are cached and can be committed or rejected.'
        }]
    },{
        title: 'Toolbars and Menus',
        samples: [{
            text: 'Basic Toolbar',
            url: 'http://extjs.com/examples/toolbar/toolbar.html',
            icon: 'basictoolbar.gif',
            desc: 'Toolbar and menus that contain various components like date pickers, sub-menus and more.'
        }]
    },{
        title: 'Templates and Lists',
        samples: [{
            text: 'Templates',
            url: 'http://extjs.com/examples/core/templates.html',
            icon: 'templates.gif',
            desc: 'A simple example of rendering views from templates bound to data objects.'
        },{
            text: 'ListView',
            url: 'http://extjs.com/examples/view/listview.html',
            icon: 'listview.gif',
            desc: 'A template driven multi-selct list view.'
		},{
            text: 'Advanced ListView',
            url: 'http://extjs.com/examples/view/chooser.html',
            icon: 'advancedlistview.gif',
            desc: 'A more customized ListView supporting sorting and filtering with multiple templates.'
		},{
            text: 'List',
            url: 'http://extjs.com/examples/list/datalist.html',
            icon: 'datalist.gif',
            desc: 'Includes both a single and multi select data list.'
        }]
    },{
        title: 'Miscellaneous',
        samples: [{
            text: 'Buttons',
            url: 'http://extjs.com/examples/misc/buttons.html',
            icon: 'buttons.gif',
            desc: 'Various button exmamples including icons, disabled, toggled, and split buttons.'
        },{
            text: 'ToolTips',
            url: 'http://extjs.com/examples/tips/tooltips.html',
            icon: 'tooltips.gif',
            desc: 'Custom tooltip with title and text.'
		},{
            text: 'DatePicker',
            url: 'http://extjs.com/examples/misc/datepicker.html',
            icon: 'datepicker.gif',
            desc: 'Component used to select a date.'
        },{
            text: 'Resizable',
            url: 'http://extjs.com/examples/misc/resizable.html',
            icon: 'resizable.gif',
            desc: 'Example of adding 8-way resizing to a content panel.'
		},{
            text: 'Draggable',
            url: 'http://extjs.com/examples/misc/draggable.html',
            icon: 'draggable.gif',
            desc: 'Examples of making any element resizable with various configuration options.'
		},{
            text: 'Fx',
            url: 'http://extjs.com/examples/misc/fx.html',
            icon: 'fx.gif',
            desc: 'Examples of different effects including sliding and moving.'
        }]
    }];

    for(var i = 0, c; c = catalog[i]; i++){
        c.id = 'sample-' + i;
    }

	var menu = Ext.get('sample-menu-inner'), 
		ct = Ext.get('sample-box-inner');
	
	var tpl = new Ext.XTemplate(
        '<div id="sample-ct">',
            '<tpl for=".">',
            '<div><a name="{id}" id="{id}"></a><h2><div unselectable="on">{title}</div></h2>',
            '<dl>',
                '<tpl for="samples">',
                    '<dd ext:url="{url}"><img title="{text}" src="thumbs/{icon}"/>',
                        '<div><h4>{text}</h4><p>{desc}</p></div>',
                    '</dd>',
                '</tpl>',
            '<div style="clear:left"></div></dl></div>',
            '</tpl>',
        '</div>'
    );

	tpl.overwrite(ct, catalog);
	
	
	var tpl2 = new Ext.XTemplate(
        '<tpl for="."><a href="#{id}" hidefocus="on" class="{cls}" id="a4{id}"><img src="http://extjs.com/s.gif" class="{iconCls}">{title}</a></tpl>'
    );
    tpl2.overwrite(menu, catalog);
	
	
	function calcScrollPosition(){
		var found = false, last;
		ct.select('a[name]', true).each(function(el){
			last = el;
			if(el.getOffsetsTo(ct)[1] > -5){
				activate(el.id)
				found = true;
				return false;
			}
		});
		if(!found){
			activate(last.id);
		}
	}
	
	var bound;
	function bindScroll(){
		ct.on('scroll', calcScrollPosition, ct, {buffer:250});
		bound = true;
	}
	function unbindScroll(){
		ct.un('scroll', calcScrollPosition, ct);
		bound = false;
	}
	function activate(id){
		Ext.get('a4' + id).radioClass('active');
	}
	
	ct.on('mouseover', function(e, t){
        if(t = e.getTarget('dd')){
            Ext.fly(t).addClass('over');
        }
    });
    ct.on('mouseout', function(e, t){
        if((t = e.getTarget('dd')) && !e.within(t, true)){
            Ext.fly(t).removeClass('over');
        }
    });
	ct.on('click', function(e, t){
        if((t = e.getTarget('dd', 5)) && !e.getTarget('a', 3)){
            var url = Ext.fly(t).getAttributeNS('ext', 'url');
			if(url){
				window.open(url);
			}
        }else if(t = e.getTarget('h2', 3, true)){
			t.up('div').toggleClass('collapsed');
		}		
    });
    
	menu.on('click', function(e, t){
		e.stopEvent();
		if((t = e.getTarget('a', 2)) && bound){
			var id = t.href.split('#')[1];
			var top = Ext.getDom(id).offsetTop;
			Ext.get(t).radioClass('active');
			unbindScroll();
			ct.scrollTo('top', top, {callback:bindScroll});
		}
	});
	
	Ext.get('samples-cb').on('click', function(e){
		var img = e.getTarget('img', 2);
		if(img){
			Ext.getDom('samples').className = img.className;
			calcScrollPosition.defer(10);
		}
	});
	
	bindScroll();
});
