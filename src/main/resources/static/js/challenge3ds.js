$('#formChallenge').submit(function(){
    var data = {
        threeDSServerTransID:$('#threeDSServerTransID').text(),
        outcome: $('#outcome').val()
    };

    var saveResponseUrl = $('#mockPgsUrl').text() + '/3ds2.0-manager/challenge/save/response';
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == XMLHttpRequest.DONE && xhttp.status !== 200) {
            return false;
        } else if (xhttp.readyState == XMLHttpRequest.DONE && xhttp.status == 200) {
            return true;
        }
    }

    xhttp.open('POST', saveResponseUrl, false);
    xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xhttp.send(JSON.stringify(data));
    console.log(xhttp.status);
});
