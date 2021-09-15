var timeleft = 2;
var errorMessageIdBack=document.getElementById("errorMessageIdBack").innerHTML;
var urlReturnFallBackPaypalPsp=document.getElementById("urlReturnFallBackPaypalPsp").value;

var downloadTimer = setInterval(function(){
  if(timeleft <= 0){
    clearInterval(downloadTimer);
    window.location.href=urlReturnFallBackPaypalPsp+'?esito=9&err_cod=10&err_desc=id_back non valido o utilizzato'
  } else {
    document.getElementById("errorMessageIdBack").innerHTML = errorMessageIdBack.replace("in 3 seconds...", 'in '+timeleft+' seconds...');;
  }
  timeleft -= 1;
}, 1000);