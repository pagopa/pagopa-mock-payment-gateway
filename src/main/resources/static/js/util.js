function copy(clickedId) {
  var copyText = document.getElementById(clickedId);
  copyText.select();
  document.execCommand("copy");
  $('#'+clickedId).attr('data-original-title', 'Copied!').tooltip('show');
  setTimeout(function() {
       $('#'+clickedId).attr('data-original-title', 'Click to copy!').tooltip('hide');
    }, 1000);
}

$(function(){
    $('input[type="text"].trim').change(function(){
        this.value = $.trim(this.value);
    });
});