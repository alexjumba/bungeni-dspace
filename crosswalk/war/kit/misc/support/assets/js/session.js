/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

SessionPanel = Ext.extend(Ext.DataView, {
    autoHeight: true,
    frame:false,
    baseCls: 'x-plain',
    cls:'demos',
    itemSelector: 'dd',
    overClass: 'over',
    
    tpl : new Ext.XTemplate(
        '<div id="session-ct">',
            '<tpl for=".">',
            '<div class="big-box"><div class="big-box-inner"><div><a name="{id}"></a><h4><span style="float:right;">{dt}</span>{title}</h4>',
            '<dl style="position: relative">',
                '<tpl for="sessions">',
                        '<dd style="float: left;',
                          'width:{[values.track == 0 ? "678" : "337"]};',
                          
                        '">',
                        
            
                        '<div style="height: {[Math.max(75, values.height)]}px">',
                       
                        '<tpl if="values.fav == 1">',
                    	'<img src="/assets/images/extcon/favoritesmall.png">',
                    	'</tpl>',
                        '<tpl if="values.details == 1">',
                        '<b class="session-hd"><a href="session.php?sid={sid}">{text}</a></b>',
                        '</tpl>',
                        '<tpl if="values.details == 0">',
                        '<b class="session-hd">{text}</b>',
                        '</tpl>',
                        '<i class="sub-hd"><i>{time} - {endtime}</i> {[values.presenter ? "- " + values.presenter + "" : ""]}</i>',
                        '<p style="padding: 5px">{desc}</p></div></dd>',
                '</tpl>',
            '<div style="clear:left"></div></dl></div></div></div>',
            '</tpl>',
        '</div>'
    ),

    onClick : function(e){
        var group = e.getTarget('h2', 3, true);
        if(group){
            group.up('div').toggleClass('collapsed');
        }else if (false){
            var t = e.getTarget('dd', 5, true);
            if(t && !e.getTarget('a', 2)){
                var url = t.getAttributeNS('ext', 'url');
                window.open(url);
            }
        }
        return SessionPanel.superclass.onClick.apply(this, arguments);
    }
});


Ext.EventManager.on(window, 'load', function(){
	

    for(var i = 0, c; c = catalog[i]; i++){
        c.id = 'sample-' + i;
    }

    var store = new Ext.data.JsonStore({
        idProperty: 'id',
        fields: ['id', 'title', 'dt', 'sessions', 'height'],
        data: catalog
    });

    new Ext.Panel({
        autoHeight: true,
        //collapsible: true,
        frame:false,
        baseCls: 'x-plain',
        //title: 'All Sessions',
        items: new SessionPanel({
            store: store
        })
    }).render('all-sessions');

});