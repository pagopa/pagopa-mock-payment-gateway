function onchangeStep0(){
 var valStep0 = $('#step0').val();
 if(valStep0 == "25"){
    $('#fieldsetStep1').removeAttr("disabled")
 }
}

$(document).ready(function() {
    onchangeStep0()
});