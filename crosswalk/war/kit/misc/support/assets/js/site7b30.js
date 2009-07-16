Ext.onReady(function(){
	
	if(!Ext.getDom('hd')){
		return;
	}
	
	var activeMenu;
	
	function createMenu(name){
		var el = Ext.get(name+'-link');
		var tid = 0, menu, doc = Ext.getDoc();
		
		var handleOver = function(e, t){
			if(t != el.dom && t != menu.dom && !e.within(el) && !e.within(menu)){
				hideMenu();
			}	
		};
				
		var hideMenu = function(){
			if(menu){
				menu.hide();
				el.setStyle('text-decoration', '');
				doc.un('mouseover', handleOver);
				doc.un('mousedown', handleDown);
			}
		}
		
		var handleDown = function(e){
			if(!e.within(menu)){
				hideMenu();
			}
		}
		
		var showMenu = function(){
			clearTimeout(tid);
			tid = 0;
			
			if (!menu) {
				menu = new Ext.Layer({shadow:'sides',hideMode: 'display'}, name+'-menu');
			}
			menu.hideMenu = hideMenu;
				
			menu.el = el;
			if(activeMenu && menu != activeMenu){
				activeMenu.hideMenu();
			}
			activeMenu = menu;
			
			if (!menu.isVisible()) {
				menu.show();
				menu.alignTo(el, 'tl-bl?');
				menu.sync();
				el.setStyle('text-decoration', 'underline');
				
				doc.on('mouseover', handleOver, null, {buffer:150});
				doc.on('mousedown', handleDown);
			}
		}
		
		el.on('mouseover', function(e){
			if(!tid){
				tid = showMenu.defer(150);				
			}
		});
		
		el.on('mouseout', function(e){
			if(tid && !e.within(el, true)){
				clearTimeout(tid);
				tid = 0;				
			}
		});
	}
	
	createMenu('products');
	createMenu('support');
	createMenu('store');
	
	// expanders
	Ext.getBody().on('click', function(e, t){
		t = Ext.get(t);
		e.stopEvent();
		
		var bd = t.next('div.expandable-body');
		bd.enableDisplayMode();
		var bdi = bd.first();
		var expanded = bd.isVisible();
		
		if(expanded){
			bd.hide();
		}else{
			bdi.hide();
			bd.show();
			bdi.slideIn('l', {duration:.2, stopFx: true, easing:'easeOut'});	
		}
		
		t.update(!expanded ? 'Hide details' : 'Show details');
			
	}, null, {delegate:'a.expander'});
	
	var gs = Ext.get('gsearch-box');
	if(gs){
		gs.on('focus', function(){
			if(gs.getValue() == 'Search with google'){
				gs.dom.value = '';
				gs.up('div').addClass('gs-active');
			}
		});
		
		gs.on('blur', function(){
			if(gs.getValue() == ''){
				gs.dom.value = 'Search with google';
				gs.up('div').removeClass('gs-active');
			}
		});
		
		if(gs.getValue() == ''){
			gs.dom.value = 'Search with google';
		}
	}
	
    /*
	
	// messages
	var msgs = [
		{text: 'Ext Enterprise Training Now Available &raquo;', url:'http://extjs.com/support/training/'},
		{text: 'Ext GWT 1.2 Release Now Available &raquo;', url:'http://extjs.com/products/gxt/'},
		{text: 'Ext 2.2.1 Release Now Available &raquo;', url:'http://extjs.com/products/extjs/download.php'}
	];
	
	var msgIndex = 0;
	var msg = Ext.get('msg'),
		msgInner = Ext.get('msg-inner'), 
		active = null;
		
	msgInner.addClassOnOver('msg-over');
	
	msg.on('click', function(){
		window.location = active.url;
	});
	
	function doUpdate(){
		msgInner.update(active.text);
		msg.slideIn('b');
	}
	
	function showMsg(index){
		if(!msgInner.hasClass('msg-over')) {
			active = msgs[index];
			if(msg.isVisible()){
				msg.slideOut('b', {callback: doUpdate});
			}else{
				doUpdate();	
			}			
		}
	}
	
	setInterval(function(){
        msgIndex = msgs[msgIndex+1] ? msgIndex+1 : 0;
        showMsg(msgIndex);      
    }, 5000);
    showMsg(0);
	
    
    var hd = Ext.get('hd');
    if(hd){
        hd.createChild({tag:'a', cls:'extcon', href: 'http://extjs.com/conference/'});
    }
    */
});
