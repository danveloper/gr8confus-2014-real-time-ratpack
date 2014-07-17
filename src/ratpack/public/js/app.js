var App = {
    context: "default",

    initNewPhoto: function () {
        var $photo = $("#photo");

        $("#add").click(function (e) {
            e.preventDefault();
            $photo.click();
        });

        $photo.change(function (e) {
            App.setContext(App.context);
            var fd = new FormData(document.getElementById("form"));
            $.ajax({
                url: '/api',
                type: 'POST',
                data: fd,
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false
            }).done(function (data) {
                $photo.val(null);
            });
        });
    },

    initWebSockets: function () {
        var photoRow = document.querySelector("ul.photo-row");

        if (!window.ws || window.ws.readyState != WebSocket.OPEN) {
            window.ws = new WebSocket("ws://" + location.host + "/ws/" + App.context);

            window.ws.onopen = function (event) {
                console.log("WebSocket opened!");
            };
            window.ws.onmessage = function (event) {
                var li = document.createElement("li");
                var img = document.createElement("img");
                img.src = "/api/" + event.data;
                li.appendChild(img);
                photoRow.appendChild(li);
            };
            window.ws.onclose = function (event) {
                var timer = setTimeout(function () {
                    console.log("Retrying connection...");
                    App.initWebSockets();
                    if (window.ws.readyState == WebSocket.OPEN) {
                        clearTimeout(timer);
                    }
                }, 1000);
            };
        }
    },

    initContextChanger: function () {
        var $context = $("#context");
        $context.change(function (e) {
            App.context = $context.val();
            window.ws.close();
        })
    },

    setContext: function (context) {
        $("#wsContext").val(App.context);
    },

    init: function () {
        App.initContextChanger();
        App.initNewPhoto();
        App.initWebSockets();
    }
};

App.init();