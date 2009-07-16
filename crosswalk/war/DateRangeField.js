Ext.namespace("Ext.ux.form");

/**
 * Widget per l'acquisizione di un periodo di tempo. Il periodo può essere compreso tra 2 date, maggiore di una certa data
 * o minore di un'altra.
 * La configurazione ammette tutti i parametri di configurazione specifici di Ext.form.DateField.
 * In più è possibile indicare labelDataFormat per configurare il formato con cui la data viene espressa nella casella combo
 * di riassunto valore campo.
 * I metodi getValue e setValue restituiscono ed ammettono rispettivamente un oggetto che rappresenta il periodo selezionato e che
 * contiene gli attributi "begin" ed "end". A questi attributi corrispondono degli oggetti JavaScript Date o null se il limite del
 * periodo non è stato indicato.
 * Viene segnalato l'evento "confirm" con i parametri evento, valore corrente quando il valore del campo viene confermato ed è valido.
 *
 * Attributi di configurazione:
 *
 * tutti quelli di DateField con in più
 *
 * fromText: Testo da usare nella label come "Da"
 * toText: Testo da usare nella label come "A"
 * invalidPeriodText: Testo da visualizzare come messaggio di periodo non valido
 * confirmText: Etichetta del pulsante "ok"
 *
 * labelDateFormat: formato da utilizzare per le date nell'etichetta
 *
 * hiddenName: se indicato vengono creati in automatico 2 campi hidden con nome hiddenName+"Begin" e hiddenName+"End" e che vengono
 * compilati con le date selezionate nel formato descritto da hiddenFormat
 * hiddenFormat: se indicato viene usato per formattare le date negli hidden
 *
 * @class Ext.ux.form.DateRangeField
 * @extends Ext.form.Field
 * @version 1.0.0 - 3/10/2008
 */
Ext.ux.form.DateRangeField = Ext.extend(Ext.form.TriggerField, {

 fromText: "",//"From",
 toText: "TO",
 invalidPeriodText: "Invalid period",
 confirmText: "Ok",

 expanded: false,
 labelDateFormat: "d/m/Y",
 allowBlank: true,

 initComponent:function() {

 Ext.ux.form.DateRangeField.superclass.initComponent.call(this);
 this.readOnly = true;

 this.popup = new Ext.Layer(
 {
 constrain: false,
 shadow: true
 });
 var elPanel = this.popup.createChild();
 var baseCfg = {};
 if(this.format)
 baseCfg.format = this.format;
 if(this.altFormats)
 baseCfg.altFormats = this.altFormats;
 if(this.disabledDates)
 baseCfg.disabledDates = this.disabledDates;
 if(this.disabledDatesText)
 baseCfg.disabledDatesText = this.disabledDatesText;
 if(this.disabledDays)
 baseCfg.disabledDays = this.disabledDays;
 if(this.disabledDaysText)
 baseCfg.disabledDaysText = this.disabledDaysText;
 if(this.invalidText)
 baseCfg.invalidText = this.invalidText;
 if(this.minValue)
 baseCfg.minValue = this.minValue;
 if(this.maxValue)
 baseCfg.maxValue = this.maxValue;

this.lang_store = new Ext.data.JsonStore({
        //url: 'index.do',
    	//autoLoad: true,
        //root: 'searchResults',
        baseParams: {
            method:   'post',
            type: 'dspace',
            ////keywords: '',//search_term,
            ////start: 0,
    //start: 0,
    //loc: '',
            ////pagesize: 20,
            //page: 0,
            ////rows: 20,
            //start: 0,
//    		sidx: 'id',
//            sord: 'DESC'         
        },
        /*params:{
    		start: 0
        },*/
        paramNames:
        {
            start: "start",//"page",    // The parameter name which specifies the start row
            limit: "rows",    // The parameter name which specifies number of rows to return
            sort: "sidx",      // The parameter name which specifies the column to sort on
            //query1: null,query2: null, query3: null, query4: null, query5: null, query6: null, query7: null, loc: null, 
            dir: "sord"		   // The parameter name which specifies the sort direction                
        },
        totalProperty: 'maxResults',
        idProperty: 'url',
        //id: 'url',
        fields: [
            {name: 'year', mapping: 'year', type: 'string'},
            {name: 'name', type: 'string'}
        ],
      //init order by status
        sortInfo:{field: 'url', direction: "DESC"},
        remoteSort: true,
        proxy: new Ext.data.HttpProxy({
            method: 'POST',
            url: '../years.do'
        }),
        listeners: {
        	load: {
	            fn: function() {
		        	this.each(function(item, index, totalItems ) {

				});
	            }
	        },
	        loadexception : function(This,o, arg,e) {
	            //////alert(e.toString());
	            ////////console.info('resuest failed :-(');
	        }     
	}
});

		// set the default sorting for the corresponding sql query
		//lang_store.setDefaultSort('id', 'DESC');
		//this.lang_store.load();

// set the default sorting for the corresponding sql query
this.lang_store.setDefaultSort('code', 'DESC');

this.langTpl = new Ext.XTemplate(
        '<tpl for="."><div class="search-item">',
            '<h3><span><img alt="{year}" title="{name}" src="../images/languages/{name}.png"/> {year}</span></h3>',
            '{description}',
        '</div></tpl>'
    );
 this.dateFrom = new Ext.form.ComboBox({
		  store: this.lang_store,
		  displayField:'year',
		  typeAhead: true,
		  mode: 'remote',
emptyText:'Select a year...',
triggerAction: 'all',
allowBlank: true,
		  loadingText: 'Searching...',
		  width: 160,
		  pageSize:15,
		  hideTrigger:false,
		  tpl: this.langTpl,
		  //applyTo: 'search',
		  itemSelector: 'div.search-item'/*,
		  onSelect: function(record){ // override default onSelect to do redirect
		      window.location =
			  String.format('main2.html?lang={0}&search={1}', record.data.code, search_term);
		  }*/
	      });
 this.dateTo = new Ext.form.ComboBox({
		  store: this.lang_store,
		  displayField:'year',
		  typeAhead: true,
		  mode: 'remote',
emptyText:'Select a year...',
triggerAction: 'all',
allowBlank: true,
		  loadingText: 'Searching...',
		  width: 160,
		  pageSize:15,
		  hideTrigger:false,
		  tpl: this.langTpl,
		  //applyTo: 'search',
		  itemSelector: 'div.search-item'/*,
		  onSelect: function(record){ // override default onSelect to do redirect
		      window.location =
			  String.format('main2.html?lang={0}&search={1}', record.data.code, search_term);
		  }*/
	      });

 new Ext.Panel({
 renderTo: elPanel,
 layout: "form",
 labelWidth: 80,
 items: [
 this.dateFrom,
 this.dateTo,
 {xtype: "button", text: this.confirmText, listeners: {"click": {fn: this.fakeconfirm, scope: this}}},
 {xtype: "button", text: "Clear", listeners: {"click": {fn: function() {
this.dateFrom.setValue("");
this.dateTo.setValue("");
this.fakeconfirm();
                }, scope: this}}}
 ]

 });

 this.addEvents("confirm");
 },

 /*onChange: function(ct, position, position2)
 {
alert("changed!!");
Ext.ux.form.DateRangeField.superclass.onChange.call(ct, position, position2);
},*/


 onRender: function(ct, position)
 {
 Ext.ux.form.DateRangeField.superclass.onRender.call(this, ct, position);
 if(this.hiddenName)
 {
 this.hiddenFromField = this.el.insertSibling({
 tag:'input',
 type:'hidden',
 name: this.hiddenName+"Begin",
 id: (this.hiddenId||this.hiddenName)+"From"
 }, "before", true);

 this.hiddenToField = this.el.insertSibling({
 tag:'input',
 type:'hidden',
 name: this.hiddenName+"End",
 id: (this.hiddenId||this.hiddenName)+"To"
 }, "before", true);
 this.el.dom.removeAttribute("name");
 }
 },

 onTriggerClick : function(e){
 Ext.ux.form.DateRangeField.superclass.onTriggerClick.call(this,e)
 if(this.expanded)
 {
 this.confirm();
 }
 else
 this.expand();
 },

fakeconfirm: function(){
this.fireEvent("change", this);
this.confirm();
},


 confirm: function()
 {
 //if(!this.isValid())
 //return;

 var label = this.getLabel();

//console.log("label: ", label);
 this.setRawValue(label);
 this.fireEvent("confirm", this, this.getValue());
 this.collapse();
 ////this.fireEvent("change", this);
 if(this.hiddenName)
 {
 var hf = this.hiddenFormat || this.format;
 var p = this.getValue();
 var res = "";

 this.hiddenFromField.value = p.begin != null ? p.begin/*.format(this.hiddenFormat)*/ : "";
 this.hiddenToField.value = p.end != null ? p.end/*.format(this.hiddenFormat)*/ : "";
 }
 Ext.ux.form.DateRangeField.superclass.setRawValue.call(this,label);
 },

 expand: function()
 {
 if(this.expanded || this.disabled)
 return;
 this.popup.alignTo(this.el, "tl-bl?");
 this.popup.show();
 this.expanded = true;
 },

 collapse: function()
 {
 if(!this.expanded)
 {
 return;
 }
 this.popup.hide();
 this.expanded = false;
 },

 isValid: function(preventMark)
 {
 /*if(!this.dateFrom.isValid() || !this.dateTo.isValid())
 {
 return false;
 }*/
 var p = this.getValue();
 /*if(!this.allowBlank && Ext.isEmpty(this.getLabel()))
 {
 if(!preventMark)
 {
 this.markInvalid(this.blankText);
 this.dateFrom.markInvalid(this.blankText);
 this.dateTo.markInvalid(this.blankText);
 }
 return false;
 }
 else if((p.begin != null) && (p.end != null) && (p.begin.getTime() > p.end.getTime()))
 {
 if(!preventMark)
 {
 this.dateTo.markInvalid(this.invalidPeriodText);
 }
 return false;
 }
 else
 {
 if(!preventMark)
 {
 this.clearInvalid();
 this.dateFrom.clearInvalid();
 this.dateTo.clearInvalid();
 }
 return true;
 }*/
/**/return true;
 },

 validate: function()
 {
 /*this.isValid(false);*/
 },

 getValue: function()
 {
 var period = {begin: null, end: null};

 var dateFrom = this.dateFrom.getValue();

 var dateTo = this.dateTo.getValue();

 /*if(Ext.isDate(dateFrom))
 {
 dateFrom.setHours(0,0,0,0);
 period.begin = dateFrom;
 }

 if(Ext.isDate(dateTo))
 {
 dateTo.setHours(23,59,59,999);
 period.end = dateTo;
 }*/

period.begin = dateFrom;
period.end = dateTo;

 return period;
 },

 getLabel: function()
 {
 var p = this.getValue();
//console.log("mmmmmmm",p);
 var res = "";
 if(p.begin != null && p.begin != undefined && p.begin != "")
 res += this.fromText+" "+p.begin;
 if(p.end != null && p.end != undefined && p.end != "")
 {
 if(res.length > 0)
 res += " ";

 res += this.toText+" "+p.end;
 }

//var p = this.getValue();
 //var res = p.begin +" TO "+ p.end;
//if((p.begin == null || p.begin == undefined) && (p.end == null || p.end == undefined))res = "";

//console.log("hgfdssdfghgfdsa", p, this, this.dateFrom, this.dateTo, res);
 return res;
 },

 setValue: function(period)
 {
 /*if(Ext.type(period) != "object")
 return;*/

//alert("settin value");
//console.log("llllll", period, this.dateFrom);

var tokens = period.split(" TO ");
 /*for(var i=0;i<tokens.length;i++)
 {
 var op = tokens[i].indexOf("<=");

}*/
//console.log(tokens);
 this.dateFrom.setValue(/*period.begin*/tokens[0]);

 this.dateTo.setValue(/*period.end*/tokens[1]);
//this.fireEvent("change", this);
 this.confirm();
this.fireEvent("change", this);
 }
});

Ext.ux.form.DateRangeField.periodToExpr = function(p, dateFormat)
{
 var res = "";
 if(p.begin) res += ">="+p.begin.format(dateFormat);
 if(p.end)
 {
 if(res.length > 0) res += "&&";
 res+= "<="+p.end.format(dateFormat);
 }
 return res;
};

Ext.ux.form.DateRangeField.exprToPeriod = function(expr, dateFormat)
{
 var p = {begin: null, end: null};

 if((Ext.type(expr) == "string") && !Ext.isEmpty(expr))
 {
 var tokens = expr.split("&&");
 for(var i=0;i<tokens.length;i++)
 {
 var op = tokens[i].indexOf("<=");
 if(op >= 0)
 {
 var date = tokens[i].substring(op + 2).trim();
 p.end = Date.parseDate(date, dateFormat);
 }

 op = tokens[i].indexOf(">=");
 if(op >= 0)
 {
 var date = tokens[i].substring(op + 2).trim();
 p.begin = Date.parseDate(date, dateFormat);
 }
 }
 }
 return p;
};



// register xtype
Ext.reg('daterangefield', Ext.ux.form.DateRangeField); 