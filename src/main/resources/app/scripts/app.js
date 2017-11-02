//home grown http lib
var http = function () {

    this.get = function(url, success, error) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            if (request.readyState == 4) {
                if (request.status == 200) {
                    success(request.responseText);
                } else {
                    error(request);
                }
            }
        };
        request.open('GET', url);
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader('Authorization', 'Bearer ' + token);
        }
        request.send();
    };

    this.post = function(url, data, contentType, success, error) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            if (request.readyState == 4) {
                if (request.status == 200) {
                    success(request.responseText);
                } else {
                    error(request);
                }
            }
        };
        request.open('POST', url);
        if (contentType) {
            request.setRequestHeader("Content-Type", contentType);
        }
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader("Authorization", "Bearer " + token);
        }
        request.send(data);
    }
}; var client = new http();

function getId(id) {
    return document.getElementById(id);
}

function storeToken(resp) {
    sessionStorage.setItem('jwt_token', resp);
}

function getStoredToken() {
    return sessionStorage.getItem('jwt_token')
}

//generic error handling
function error(errorResponse) {
    client.get('pages/error.html', function(resp) {
        Mustache.parse(resp);
        getId('content').innerHTML = Mustache.render(resp, {
            errCode: errorResponse.status
        });
    }, function(resp) {
        error(resp);
    });
}

//page rendering
(function() {
    function generateHTML(url, id) {
        client.get(url, function(resp) {
            getId(id).innerHTML = resp;
        }, function(resp) {
            error(resp);
        });
    }

    //load tags
    window.addEventListener("load", function() {
        var template = getId('tags-template').innerHTML;
        Mustache.parse(template);
        client.get('api/articles/tagStubs', function (resp) {
            getId('tags').innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        }, function(resp) {
            error(resp);
        });

        if (!getStoredToken()) {
            getId('bookmarked').setAttribute('class', 'inactive-link');
        } 
    });

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
        'register'  : () => {generateHTML('pages/register.html', 'content')},
        'settings'  : () => {generateHTML('pages/settings.html', 'content')},
        'add'       : () => {generateHTML('pages/addFeeds.html', 'content')},
        'error'     : () => {generateHTML('pages/error.html', 'content')}
    });
    
    router.on('tagged', function(params, query) {
        generateHTML('api/articles/tagged?tag=' + query, 'content');
    }).resolve();
    
    router.resolve();
})();

//login
function login() {
    var formData = new FormData();
    formData.append("email", getId("emailInput").value);
    formData.append("password", getId("passwordInput").value);
    client.post('api/user/login', formData, null, function(resp) {
        storeToken(resp);
        window.location.assign('#!frontpage');
        window.location.reload();
    }, function(resp) {
        error(resp);
    });
}

//register
function register() {
    var newUser = {};
    newUser.email = getId('emailInput').value;
    newUser.password = getId('passwordInput').value;
    client.post('api/user/new', JSON.stringify(newUser), "application/json;charset=UTF-8", function(resp) {
        //do work
        console.log(resp);
    }, function(resp) {
        error(resp);
    });
}

//mock form enter behavior
function searchKeyPress(e) {
    e = e || window.event; //look for window.event in case event isn't passed in
    if (e.keyCode == 13) {
        getId('btnSearch').click();
        return false;
    }
    return true;
}


