

Ext.onReady(function(){

	Ext.QuickTips.init();

	var remoteUrl = 'data/RemoteComponent1.json';
	var arrayUrl = 'data/RemoteComponent3.json';
	var tabId = 'tabpanel';
	var tabIndex = 0;
	var liteTab = false;
	var deferedTab = false;
	var mainPanel = false;
	
	var getRemoteComponentPlugin = function(){
		return new Ext.ux.Plugin.RemoteComponent({
			url : remoteUrl,
			mask: 'demo',
			maskConfig: {
				msg: 'remoting...'
			}
		}); 
	};

	var getLiteRemoteComponentPlugin = function(){
		return new Ext.ux.Plugin.LiteRemoteComponent({
			url : remoteUrl,
			mask: Ext.getDom('demo')
		}); 
	};

	var getDeferedRemoteComponentPlugin = function(){
		return new Ext.ux.Plugin.RemoteComponent({
			url : remoteUrl,
			loadOn: 'show',
			mask: Ext.getDom('demo')
		}); 
	};

	var getMainPanel = function(){
		if(!mainPanel){
			mainPanel = new Ext.TabPanel({
			    activeTab: 0,
				resizeTabs:true, // turn on tab resizing
		        minTabWidth: 160,
		        tabWidth:160,
		        enableTabScroll:true,
		        width:'auto',
		        height:330,
				title: 'RemoteComponent-Demo',
				autoShow: true,
		        defaults: {autoScroll:true},
				items: [{
					title:'Instant Tab',
					closable: true,
					plugins: [getRemoteComponentPlugin()]
				}]
			});		
		}		
		return mainPanel;
	};

	var addRemoteTab = function(){
		tabIndex += 1;
		return getMainPanel().add({
	        title: 'RemoteComponent ' + tabIndex,
			id: 'tab' + tabIndex,
			closable: true,
			plugins: new Ext.ux.Plugin.RemoteComponent({
				url : remoteUrl
			}),
			autoShow: true
			}
		).show();
	};

	var addLiteTab = function(){
		if(!liteTab){			
			liteTab =  getMainPanel().add({
		        title: 'LiteRemoteComponent',
				closable: true,
				plugins: [getLiteRemoteComponentPlugin()],
				autoShow: true
				} 
			).show();
			liteTab.on('destroy', function(){
				liteTab= false;
			})						
		}		
		return liteTab;
	};

	var addDeferedTab = function(){
		if(!deferedTab){			
			deferedTab =  getMainPanel().add({
		        title: 'defered RemoteComponent',
				closable: true,
				plugins: [getDeferedRemoteComponentPlugin()],
				autoShow: true
				}
			);
			deferedTab.on('destroy', function(){
				deferedTab = false;
			});						
		}		
		return deferedTab;
	};

	var addArrayTab = function(){
		tabIndex += 1;
		return getMainPanel().add({
	        title: 'RemoteComponent ' + tabIndex,
			id: 'tab' + tabIndex,
			closable: true,
			activeTab: 0,
				layout : 'form',
				defaults: {xtype: 'textfield'},
				items: [{fieldLabel: 'Field from Config', name: 'test'}],
				title: 'Arrays',
				bodyStyle: {padding: 10},
				plugins: new Ext.ux.Plugin.RemoteComponent({
					url : arrayUrl,
					mask: 'demo',
					maskConfig: {
						msg: 'remoting...'
					}						
				}) 
			}
		).show();
	};



	var addMixedTab = function(){
		tabIndex += 1;
		var panelId = 'tab' + tabIndex;
		// array for remote components
		var remoteFields = [];
		var button = new Ext.Button({id: 'add-fie',text: 'Add remote fields', iconCls:'icon-plug-add'});
		var plug = new Ext.ux.Plugin.RemoteComponent({
			url : arrayUrl,
			loadOn: [{
				event: 'click',
				scope: button
			}],
			mask: 'tab' + tabIndex,
			maskConfig: {
				msg: 'loading Array config...'
			},
			purgeSubscribers: false,
			listeners: {
				'success' : {fn: function(){
				 	button.disable();
				 	button2.enable();
				 	button3.enable();
				}},
				'beforecomponshow' : {fn: function(cmp){
				 	// reference new components, so we can destroy them later 
					remoteFields.push(cmp);
										 	
				}}
			}								
		});
		var button2 = new Ext.Button({id: 'add-dis',
			text: 'disable remote fields',
			iconCls:'icon-plug-disable',
			disabled: true, 
			handler : function(){
				
				Ext.each(remoteFields, function(f){					
					f.disable();
				});
				this.disable();
				Ext.getCmp(panelId).setTitle('Fields disabled');
			}
		});
		var button3 = new Ext.Button({id: 'add-rem',
			text: 'remove remote fields',
			iconCls:'icon-plug-delete',
			disabled: true, 
			handler : function(){
				
				Ext.each(remoteFields, function(f){
					// workaround to remove fieldLabels
					var formItem = Ext.get(f.getEl()).findParent('div.x-form-item');
					if(formItem){
						 Ext.get(formItem).removeAllListeners().remove()
					 }
					// remove fields
					f.ownerCt.remove(f);
				});
				// reset remoteFields
				remoteFields = [];
				button2.disable();
				this.disable();
				button.enable();
				// re-attach single listners to button 
				button.on('click', function(){
					plug.load();
				}, button, {single: true});
				Ext.getCmp(panelId).setTitle('Fields removed');
			}
			
		});
		return getMainPanel().add({
	        title: 'RemoteComponent ' + tabIndex,
			id: panelId,
			closable: true,
			activeTab: 0,
			layout : 'form',
			defaults: {xtype: 'textfield'},
			// config field
			items: [{fieldLabel: 'Field from Config', name: 'test'}],
			tbar: [button, button2, button3],
			title: 'mixed',
			bodyStyle: {padding: 10},
			plugins: plug 
			
		}).show();
	};

		
    var p = new Ext.Panel({
        title: 'Ext.ux.Plugin.RemoteComponent Demo',
        collapsible:true,
        id: 'demo-panel',
        renderTo: Ext.get('demo'),
        width:600,
		bbar : [
			new Ext.Toolbar.TextItem('Add Tabs w/ RemoteComponent-Plugins: '),
			{
				text: ' default ',
				id: 'add-default',
				handler: addRemoteTab,
				tooltip: 'Add RemoteComponent Tab',
				tooltipType : 'qtip'
			},
			new Ext.Toolbar.Separator(),
			{
				text: ' lite ',
				id: 'add-lite',
				handler: addLiteTab,
				tooltip: 'Load LiteRemoteComponent',
				tooltipType : 'qtip'
			},
			new Ext.Toolbar.Separator(),
			{
				text: ' defered ',
				id: 'add-defered',			
				xtype: 'button',
				handler: addDeferedTab,
				tooltip: 'Load defered RemoteComponent / lazy loading',
				tooltipType : 'qtip'
			},
			new Ext.Toolbar.Separator(),
			{
				text: ' array ',	
				id: 'add-array',		
				xtype: 'button',
				handler: addArrayTab,
				tooltip: 'Load an Array of component configs',
				tooltipType : 'qtip'
			},
			new Ext.Toolbar.Separator(),
			{
				text: ' mixed ',
				id: 'add-mixed',			
				xtype: 'button',
				handler: addMixedTab,
				tooltip: 'add/remove array of components with button triggers',
				tooltipType : 'qtip'
			}	
		],
        items: getMainPanel()
    });
});



