(function() {

    //home grown http lib
    var http = function () {
        this.get = function (url, callback) {
            var request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (request.readyState == 4) {
                    if (request.status == 200) {
                        callback(request.responseText);
                    } else {
                        error(request);
                    }
                }
            };
            request.open('GET', url);
            request.send();
        }
    }; var client = new http();

    function getId(id) {
        return document.getElementById(id);
    }

    function generateHTML(url, id) {
        client.get(url, function (resp) {
            getId(id).innerHTML = resp;
        });
    }

    //load tags
    window.onload = function () {
        var template = getId('tags-template').innerHTML;
        Mustache.parse(template);
        client.get('api/articles/tagStubs', function (resp) {
            getId('tags').innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        });
    };

    function error(errorResponse) {
        client.get('pages/error.html', function (resp) {
            Mustache.parse(resp);
            getId('content').innerHTML = Mustache.render(resp, {
                errCode: errorResponse.status
            });
        });
    }

    //app routing
    var router = new Navigo(null, true, '#!');

    //root view
    router.on(function () {
        generateHTML('api/articles/frontpage', 'content');
    });
    
    router.on({
        'frontpage' : () => {generateHTML('api/articles/frontpage', 'content')},
        'all'       : () => {generateHTML('api/articles/all', 'content')},
        'bookmarked': () => {generateHTML('api/articles/bookmarked', 'content')},
        'about'     : () => {generateHTML('pages/about.html', 'content')},
        'login'     : () => {generateHTML('pages/login.html', 'content')},
        'settings'  : () => {generateHTML('pages/settings.html', 'content')},
        'add'       : () => {generateHTML('pages/addFeeds.html', 'content')},
        'error'     : () => {generateHTML('pages/error.html', 'content')}
    });
    
    router.on('tagged', function (params, query) {
        generateHTML('api/articles/tagged?tag=' + query, 'content');
    }).resolve();
    
    
    router.resolve();

})();

