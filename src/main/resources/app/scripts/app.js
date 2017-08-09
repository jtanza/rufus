
(function() {

    var http = function () {
        this.get = function (url, callback) {
            var request = new XMLHttpRequest();
            request.onreadystatechange = function () {
                if (request.readyState == 4) {
                    if (request.status == 200) {
                        callback(request.responseText);
                    } else {
                        //TODO error handling for !200
                        window.location.replace('#!error');
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

    window.onload = function () {
        var template = getId('tags-template').innerHTML;
        Mustache.parse(template);
        client.get('/api/articles/tagStubs', function (resp) {
            getId('tags').innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        });
    };


    //app routing
    var router = new Navigo(null, true, '#!');
    
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

    //root view
    router.on(function () {
        generateHTML('api/articles/frontpage', 'content');
    });

    router.resolve();

})();


