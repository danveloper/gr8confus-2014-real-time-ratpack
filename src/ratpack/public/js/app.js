(function () {
    var $photo = $("#photo");

    $("#add").click(function (e) {
        e.preventDefault();
        $photo.click();
    });

    $photo.change(function (e) {
        var fd = new FormData(document.getElementById("form"));
        $.ajax({
            url: '/api',
            type: 'POST',
            data: fd,
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false
        }).done(function (data) {
            $(".photo-row").append("<li><img src='/api/" + data.name + "'></li>");
            $photo.val(null);
        });
    });
})();