/*
  Utility functions. 
 */
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
        request.open("GET", url);
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader("Authorization", "Bearer " + token);
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
        request.open("POST", url);
        if (contentType) {
            request.setRequestHeader("Content-Type", contentType);
        }
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader("Authorization", "Bearer " + token);
        }
        request.send(data);
    };
    this.put = function(url, data, contentType, success, error) {
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
        request.open("PUT", url);
        if (contentType) {
            request.setRequestHeader("Content-Type", contentType);
        }
        var token = getStoredToken();
        if (token) {
            request.setRequestHeader("Authorization", "Bearer " + token);
        }
        request.send(data);
    };
}; var client = new http();

function getId(id) {
    return document.getElementById(id);
}

function storeToken(resp) {
    sessionStorage.setItem("jwt_token", resp);
}

function getStoredToken() {
    return sessionStorage.getItem("jwt_token");
}

function generateHTML(url, id) {
    client.get(url, function(resp) {
        getId(id).innerHTML = resp;
    }, function(resp) {
        genericErrorPage(resp);
    });
}

function genericErrorPage(errorResponse) {
    client.get("pages/error.html", function(resp) {
        Mustache.parse(resp);
        getId("content").innerHTML = Mustache.render(resp, {
            errCode: errorResponse.status
        });
    }, function(resp) {
        console.log("ruh-oh could not fetch error page, letting errors crop up to client " + resp);
        window.location.reload();
    });
}

/*
  Page rendering: initial app state, path routing etc.
 */
(function() {
    window.addEventListener("load", function() {
        //load tags
        var template = getId("tags-template").innerHTML;
        Mustache.parse(template);
        client.get("api/articles/tagStubs", function (resp) {
            getId("tags").innerHTML = Mustache.render(template, {tags: JSON.parse(resp)});
        }, function(resp) {
            genericErrorPage(resp);
        });

        //set login/logout based upon current session
        if (!getStoredToken()) {
            getId("bookmarked").setAttribute("class", "inactive-link");
            getId("settings").setAttribute("class", "inactive-link");
        } else {
            var login = getId("login");
            login.innerHTML = "Logout";
            login.removeAttribute("href");
            //logout
            login.onclick = function() {
                sessionStorage.removeItem("jwt_token");
                window.location.href = '/#!all';
            };
        }
    });

    //app routing
    var router = new Navigo(null, true, "#!");

    //root view
    router.on(function () {
        generateHTML("api/articles/frontpage", "content");
    });

    router.on({
        "frontpage" : function () {
           generateHTML("api/articles/frontpage", "content");
        },
        "all" :  function () {
            generateHTML("api/articles/all", "content");
        },
        "bookmarked": function () {
            generateHTML("api/articles/bookmarked", "content")
        },
        "about" : function () {
            generateHTML("pages/about.html", "content")
        },
        "login" : function () {
            generateHTML("pages/login.html", "content")
        },
        "register" : function () {
            generateHTML("pages/register.html", "content")
        },
        "add" : function () {
            generateHTML("pages/addFeeds.html", "content")
        },
        "error" : function () {
            generateHTML("pages/error.html", "content")
        }
    });

    router.on("tagged", function(params, query) {
        generateHTML("api/articles/tagged?tag=" + query, "content");
    }).resolve();

    router.on("settings", function (params, query) {
        generateHTML("pages/settings.html", "content");
        client.get("pages/sources.mustache", function (template) {
            Mustache.parse(template);
            client.get("api/articles/userFeeds", function (resp) {
                getId("settingsContent").innerHTML = Mustache.render(template, {sources: JSON.parse(resp)});
            }, function(resp) {
                genericErrorPage(resp);
            });
        });
    });

    router.resolve();
})();

/*
  User action functions, mostly executed through element onclick events
 */
function userLogin() {
    var formData = new FormData();
    formData.append("email", getId("emailInput").value);
    formData.append("password", getId("passwordInput").value);
    client.post("api/user/login", formData, null, function(resp) {
        storeToken(resp);
        window.location.assign("#!frontpage");
        window.location.reload();
    }, function(resp) {
        genericErrorPage(resp);
    });
}

function registerUser() {
    var newUser = {};
    newUser.email = getId("emailInput").value;
    newUser.password = getId("passwordInput").value;

    var starterFeeds = [];
    var inputs = document.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        if(inputs[i].type == "checkbox" && inputs[i].checked) {
            starterFeeds.push(inputs[i].id);
        }
    }
    newUser.starterFeeds = starterFeeds;

    client.post("api/user/new", JSON.stringify(newUser), "application/json;charset=UTF-8", function(resp) {
        storeToken(resp);
        window.location.assign("#!frontpage");
        window.location.reload();
    }, function(resp) {
        genericErrorPage(resp);
    });
}

//subscribe to new feeds
function subscribe() {
    var feeds = getId("subscriptionFeeds").value;
    var feedData = JSON.stringify(feeds.split(" "));
    client.post("api/articles/new", feedData, "application/json;charset=UTF-8", function(resp) {
        alert(resp);
        window.location.assign("#!add");
        window.location.reload();
    }, function (resp) {
        genericErrorPage(resp);
    });
}

//mock form enter behavior
function searchKeyPress(e) {
    e = e || window.event; //look for window.event in case event isn"t passed in
    if (e.keyCode == 13) {
        getId("btnSearch").click();
        return false;
    }
    return true;
}

function unsubscribe(url, source) {
    if (confirm("Are you sure you would like to unsubscribe from " + source  + " ?")) {
        client.put("api/articles/unsubscribe", url, "application/json;charset=UTF-8", function (resp) {
            getId(url).remove();
        }, function (resp) {
            genericErrorPage(resp);
        });
    }
}

function setFrontpage(url, id) {
    var path = (getId(id).checked) ? "api/articles/setFrontpage" : "api/articles/removeFrontpage";
    client.put(path, url, "application/json;charset=UTF-8", function (resp) {
    }, function (resp) {
        genericErrorPage(resp);
    });
}

