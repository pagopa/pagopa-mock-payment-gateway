$(function() {
    generateOk("rand")
})

function generateOk(cardSelected) {
    generate(cardSelected, "00", "newCardOk", "imgCardOk");
}

function generate(cardSelected, code, labelCard, labelImg) {
   var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            document.getElementById(labelCard).value = xhr.response['cardNumber']
            document.getElementById(labelImg).src=xhr.response['urlImg']
        }
    }
    const params = new URLSearchParams({
        type: cardSelected,
        returnCode: code
    });

    xhr.open('GET', '/cc/generateCard?'.concat(params.toString()), true);
    xhr.responseType = 'json';
    xhr.send();
}