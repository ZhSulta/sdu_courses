var i18n=(function(){var a={};function b(c,f){if(_.isString(c)&&c.indexOf(".")!=-1){c=c.split(".")}if(!_.isArray(c)){if(!c){c=[]}else{c=[c]}}var e=a;for(var d in c){if(typeof e[c[d]]=="undefined"){if(f){e[c[d]]={}}else{return undefined}}e=e[c[d]]}return e}return{node:function(c){return b(c)},populate:function(d,c){node=b(d,true);_.extend(node,c)},trans:function(d,c){node=b(d);if(_.isFunction(node)){return node(c)}else{if(_.isString()){return node}else{console.log("Node "+$.dump(d)+" is not translatable. Value: "+$.dump(node));return""}}},ucfirst:function(c){return c.substr(0,1).toUpperCase()+c.substr(1,c.length)},}})();
i18n.locale="en";
i18n.populate("ui",{datepicker:{closeText:"Done",prevText:"Prev",nextText:"Next",currentText:"Today",monthNames:["January","February","March","April","May","June","July","August","September","October","November","December"],monthNamesShort:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],dayNames:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],dayNamesShort:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],dayNamesMin:["Su","Mo","Tu","We","Th","Fr","Sa"],weekHeader:"Wk",dateFormat:"dd/mm/yy",firstDay:1,isRTL:false,showMonthAfterYear:false,yearSuffix:""}});