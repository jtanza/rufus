(function() {
    
    function generateHTML(url, id) {
        var request = new XMLHttpRequest();
        request.open('GET', url);
        request.onload = function () {
            document.getElementById(id).innerHTML = request.responseText;
        };
        request.send();
    }

    //app routing
    var router = new Navigo(null, true, '#!');
    //root view
    router.on(function () {
        generateHTML('api/articles/frontpage', 'articles');
    });
    router.on('/frontpage', function () {
        generateHTML('api/articles/frontpage', 'articles');
    }).resolve();
    router.on('/all', function () {
        generateHTML('api/articles/all', 'articles');
    }).resolve();
    
})();


