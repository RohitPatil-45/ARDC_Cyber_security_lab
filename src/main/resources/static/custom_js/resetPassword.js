$('document').ready(function(){	
//	
//	 $(".toggle-password").click(function() {
//	        var inputField = $($(this).data("toggle"));
//	        var icon = $(this).find("i");
//
//	        if (inputField.attr("type") === "password") {
//	            inputField.attr("type", "text");
//	            icon.removeClass("fa-eye-slash").addClass("fa-eye");
//	        } else {
//	            inputField.attr("type", "password");
//	            icon.removeClass("fa-eye").addClass("fa-eye-slash");
//	        }
//	    });
	
	var password = document.getElementById("encrytedPassword")
	var confirmPassword = document.getElementById("confirmPassword");
	
	function validatePassword(){
	  if(password.value != confirmPassword.value) {
	    confirmPassword.setCustomValidity("Passwords Don't Match");
	  } else {
	    confirmPassword.setCustomValidity('');
	  }
	}	
	password.onchange = validatePassword;
	confirmPassword.onkeyup = validatePassword;		
	//confirmPassword.onchange = validatePassword;		
	
	$('#encrytedPassword').keyup(function(){
        var pwd = $(this).val();
        var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if(pattern.test(pwd)){
        	password.setCustomValidity('');
            $('#adduserForm').prop('disabled', false);
        } else {
        	password.setCustomValidity('Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.');
           // $('#adduserForm').prop('disabled', true);
        }
    });
	

});

var oldPass = $("#oldPass input");
var icon1 = $("#oldPass i");
icon1.on('click', function (event) {
    event.preventDefault();

    if (oldPass.attr("type") === "text") {
    	oldPass.attr('type', 'password');
        icon1.addClass("fa-eye-slash");
        icon1.removeClass("fa-eye");

    } else if (oldPass.attr("type") === "password") {
    	oldPass.attr('type', 'text');
        icon1.removeClass("fa-eye-slash");
        icon1.addClass("fa-eye");
    }
});


var newPass = $("#newPass input");
var icon2 = $("#newPass i");
icon2.on('click', function (event) {
    event.preventDefault();

    if (newPass.attr("type") === "text") {
    	newPass.attr('type', 'password');
    	icon2.addClass("fa-eye-slash");
    	icon2.removeClass("fa-eye");

    } else if (newPass.attr("type") === "password") {
    	newPass.attr('type', 'text');
    	icon2.removeClass("fa-eye-slash");
    	icon2.addClass("fa-eye");
    }
});

var confirmPass = $("#confirmPass input");
var icon3 = $("#confirmPass i");
icon3.on('click', function (event) {
    event.preventDefault();

    if (confirmPass.attr("type") === "text") {
    	confirmPass.attr('type', 'password');
    	icon3.addClass("fa-eye-slash");
    	icon3.removeClass("fa-eye");

    } else if (confirmPass.attr("type") === "password") {
    	confirmPass.attr('type', 'text');
    	icon3.removeClass("fa-eye-slash");
    	icon3.addClass("fa-eye");
    }
});
   

function validateOldPassword(password){
	$.ajax({
		url : '/users/validateOldPassword',
		data : {
			"password" : password
		},
		success : function(result) {
			if(result != "match")
			{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: "Old Password doesn't Match",
					showConfirmButton: false,
					timer: 3000
				})
				$('#resetPassBtn').prop( "disabled", true );
			}
			else{
				$('#resetPassBtn').prop( "disabled", false );
			}
		}
	});
}

