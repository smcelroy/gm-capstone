/* 
 * This javascript file will handle the login form.
 * It will contain an ajax call that calls the REST endpoint that processes the login
 * form.
 */


$("#loginForm").submit(function(event) {
    event.preventDefault();
    //hide any previous errors
    $("#failure").hide();
    
    var email = $("#login_email").val();
    var password = $("#login_password").val();

    var postFields = {
        email: email,
        password: password,
    };

    $.ajax({
        type: "POST",
        data: postFields,
        cache: false,
        url: "api/users/authenticate",
        success: function(data) {
                    
            if (data.result === true) {
            	$.cookie("utoken", data.id, { path: '/' });
            	$.cookie("uadmin", data.admin, { path: '/' });
            	$.cookie("ufname", data.firstName, { path: '/' });
                if(data.admin){
                	window.open("inside/#/admin/manageUsers", "_self");
                }else{
                	window.open("inside/#/profile", "_self");
                }
            } else {
                console.log("failure");
                //display error
                $("#failure-message").html("The username password combination is not valid.");
                $("#failure").show();
                
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(textStatus);
             $("#failure-message").html("There was a problem attempting to log you in. Please try again.");
             $("#failure").show();
        }
    });

    return false;
});
