$(document).ready(function() {
    var url = window.location;
    $('ul.navbar-nav a').filter(function() {
        return this.href == url;
    }).closest('li[class^="nav-item"]').addClass('active');

    $('ul.navbar-nav div.dropdown-menu a').filter(function() {
        return this.href == url;
    }).addClass('active');
});