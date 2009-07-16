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

 fromText: "From",
 toText: "To",
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

 this.dateFrom = new Ext.form.DateField(Ext.apply({fieldLabel: this.fromText}, baseCfg));
 this.dateTo = new Ext.form.DateField(Ext.apply({fieldLabel: this.toText}, baseCfg));

 new Ext.Panel({
 renderTo: elPanel,
 layout: "form",
 labelWidth: 40,
 items: [
 this.dateFrom,
 this.dateTo,
 {xtype: "button", text: this.confirmText, listeners: {"click": {fn: this.confirm, scope: this}}}
 ]

 });

 this.addEvents("confirm");
 },

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

 confirm: function()
 {
 if(!this.isValid())
 return;

 var label = this.getLabel();
 this.setRawValue(label);
 this.fireEvent("confirm", this, this.getValue());
 this.collapse();
 this.fireEvent("change", this);
 if(this.hiddenName)
 {
 var hf = this.hiddenFormat || this.format;
 var p = this.getValue();
 var res = "";

 this.hiddenFromField.value = p.begin != null ? p.begin.format(this.hiddenFormat) : "";
 this.hiddenToField.value = p.end != null ? p.end.format(this.hiddenFormat) : "";
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
 if(!this.dateFrom.isValid() || !this.dateTo.isValid())
 {
 return false;
 }
 var p = this.getValue();
 if(!this.allowBlank && Ext.isEmpty(this.getLabel()))
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
 }

 },

 validate: function()
 {
 this.isValid(false);
 },

 getValue: function()
 {
 var period = {begin: null, end: null};

 var dateFrom = this.dateFrom.getValue();

 var dateTo = this.dateTo.getValue();

 if(Ext.isDate(dateFrom))
 {
 dateFrom.setHours(0,0,0,0);
 period.begin = dateFrom;
 }

 if(Ext.isDate(dateTo))
 {
 dateTo.setHours(23,59,59,999);
 period.end = dateTo;
 }

 return period;
 },

 getLabel: function()
 {
 var p = this.getValue();
 var res = "";
 if(p.begin != null)
 res += this.fromText+" "+p.begin.format(this.labelDateFormat);
 if(p.end != null)
 {
 if(res.length > 0)
 res += " ";
 res += this.toText+" "+p.end.format(this.labelDateFormat);
 }
 return res;
 },

 setValue: function(period)
 {
 if(Ext.type(period) != "object")
 return;

 this.dateFrom.setValue(period.begin);

 this.dateTo.setValue(period.end);
 this.confirm();
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