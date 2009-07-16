Ext.namespace("Ext.ux.form");

Ext.ux.form.HistoryClearableComboBox = function(config) {
	
    Ext.ux.form.HistoryClearableComboBox.superclass.constructor.call(this, config);
	
    this.addEvents({
	'change' : true
    });

    this.store = new Ext.data.SimpleStore({
        fields: ['query'],
	data: []
    });

    this.historyRecord = Ext.data.Record.create([
        {name: 'query', type: 'string'}
    ]);
	
	this.triggerConfig = {
       tag:'span', cls:'x-form-twin-triggers', style:'padding-right:2px',  // padding needed to prevent IE from clipping 2nd trigger button
       cn:[{tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger", style:config.hideComboTrigger?"display:none":""},
           {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger x-form-clear-trigger", style: config.hideClearTrigger?"display:none":""}
    ]};
};

Ext.extend(Ext.ux.form.HistoryClearableComboBox, Ext.form.ComboBox, {
		   
    store: undefined,
    displayField: 'query',
    typeAhead: false,
    mode: 'local',
    triggerAction: 'all',
    hideTrigger: false,
    historyRecord: undefined,
    maxInHistory: 10,
    rememberOn: 'enter',

    getTrigger : function(index){
        return this.triggers[index];
    },

    initTrigger : function(){
        var ts = this.trigger.select('.x-form-trigger', true);
        this.wrap.setStyle('overflow', 'hidden');
        var triggerField = this;
        ts.each(function(t, all, index){
            t.hide = function(){
                var w = triggerField.wrap.getWidth();
                this.dom.style.display = 'none';
                triggerField.el.setWidth(w-triggerField.trigger.getWidth());
            };
            t.show = function(){
                var w = triggerField.wrap.getWidth();
                this.dom.style.display = '';
                triggerField.el.setWidth(w-triggerField.trigger.getWidth());
            };
            var triggerIndex = 'Trigger'+(index+1);

            if(this['hide'+triggerIndex]){
                t.dom.style.display = 'none';
            }
            t.on("click", this['on'+triggerIndex+'Click'], this, {preventDefault:true});
            t.addClassOnOver('x-form-trigger-over');
            t.addClassOnClick('x-form-trigger-click');
        }, this);
        this.triggers = ts.elements;
    },

    onTrigger1Click : function() {this.onTriggerClick()},   // pass to original combobox trigger handler
	
    onTrigger2Click : function() {this.reset()},             // clear contents of combobox

    onRender: function(ct) {
		Ext.ux.form.HistoryClearableComboBox.superclass.onRender.call(this, ct);
    },
    
    initEvents : function() {
		Ext.ux.form.HistoryClearableComboBox.superclass.initEvents.call(this);
		this.el.on("keyup", this.onHistoryKeyUp, this);
    },

    historyChange : function(value) {
    	var value = this.getValue().replace(/^\s+|\s+$/g, "");
        if (value.length==0) return;
        this.store.clearFilter();
        var vr_insert = true;
		if (this.rememberOn=="all") {
    	 	this.store.each(function(r) {
          	    if (r.data['query'].indexOf(value)==0) {
        	        // backspace
           	        vr_insert = false;
           	        return false;
           	    }else if (value.indexOf(r.data['query'])==0) {
           	    	// forward typing
               		this.store.remove(r);
           	    }
        	});
	    }
        if (vr_insert==true) {
    	    this.store.each(function(r) {
        	    if (r.data['query']==value) {
            	    vr_insert = false;
                }
            });
       	}
        if (vr_insert==true) {
        	var vr = new this.historyRecord({query: value});
            this.store.insert(0, vr);
       	}
        var ss_max = this.maxInHistory;
        if (this.store.getCount()>ss_max) {
        	var ssc = this.store.getCount();
            var overflow = this.store.getRange(ssc-(ssc-ss_max), ssc);
            for (var i=0; i<overflow.length; i++) {
            	this.store.remove(overflow[i]);
            }
    	}
    },
    
    onHistoryKeyUp : function(e) {
		if ( (this.rememberOn=="enter" && e.getKey()==13) || (this.rememberOn=="all") ) {
		    this.historyChange(this.getValue());
		    this.fireEvent('change', this.getValue());
		}
    },
    
    setValue : function(v) {
		Ext.ux.form.HistoryClearableComboBox.superclass.setValue.call(this, v);
		this.fireEvent('change', this.getValue());
    },
    
    clearValue : function() {
		this.setValue("");
    },
    
    EOJ : function(){}
});