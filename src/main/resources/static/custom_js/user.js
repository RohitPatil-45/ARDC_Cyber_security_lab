var emailFlag = 0;
var mobileFlag = 0;
var usernameFlag = 0;

$('document').ready(function() {
	var password = document.getElementById("encrytedPassword")
	var confirmPassword = document.getElementById("confirmPassword");
	var role = document.getElementById("userRole");
	var switchID = document.getElementById("switch_id");
	var generationType = document.getElementById("generationType");
	var emailID = document.getElementById("email")

	$('#email').keyup(function() {
		var email = $(this).val();
		var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
		if (emailPattern.test(email)) {
			emailID.setCustomValidity('');
			$('#adduserForm').prop('disabled', false);
		} else {
			emailID.setCustomValidity('Please Enter valid Email Address');
			// $('#adduserForm').prop('disabled', true);
		}
	});

	function validatePassword() {
		if (password.value != confirmPassword.value) {
			confirmPassword.setCustomValidity("Passwords Don't Match");
		} else {
			confirmPassword.setCustomValidity('');
		}

	}

	password.onchange = validatePassword;
	confirmPassword.onkeyup = validatePassword;

	//	$('#userRole').keyup(function(){
	//        var role = $(this).val();
	//        if(role === ""){
	//        	this.setCustomValidity('Please select role');
	//        } else {
	//        	this.setCustomValidity('');
	//            $('#adduserForm').prop('disabled', false);
	//        }
	//    });

	//	$('#switch_id').keyup(function(){
	//        var switchID = $(this).val();
	//        if(switchID != ''){
	//        	this.setCustomValidity('');
	//            $('#adduserForm').prop('disabled', false);
	//        } else {
	//        	this.setCustomValidity('Please select switch');
	//        }
	//    });
	//	
	//	$('#generationType').keyup(function(){
	//        var generationType = $(this).val();
	//        if(generationType != ''){
	//        	this.setCustomValidity('');
	//            $('#adduserForm').prop('disabled', false);
	//        } else {
	//        	this.setCustomValidity('Please select generation');
	//        }
	//    });
	//	
	$('#encrytedPassword').keyup(function() {
		var pwd = $(this).val();
		var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
		if (pattern.test(pwd)) {
			password.setCustomValidity('');
			$('#adduserForm').prop('disabled', false);
		} else {
			password.setCustomValidity('Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.');
			// $('#adduserForm').prop('disabled', true);
		}
	});

});

var input = $("#show_hide_password input");
var icon = $("#show_hide_password i");
icon.on('click', function(event) {
	event.preventDefault();

	if (input.attr("type") === "text") {
		input.attr('type', 'password');
		icon.addClass("fa-eye-slash");
		icon.removeClass("fa-eye");

	} else if (input.attr("type") === "password") {
		input.attr('type', 'text');
		icon.removeClass("fa-eye-slash");
		icon.addClass("fa-eye");
	}
});

var input1 = $("#show_hide_confirmPassword input");
var icon1 = $("#show_hide_confirmPassword i");
icon1.on('click', function(event) {
	event.preventDefault();

	if (input1.attr("type") === "text") {
		input1.attr('type', 'password');
		icon1.addClass("fa-eye-slash");
		icon1.removeClass("fa-eye");

	} else if (input1.attr("type") === "password") {
		input1.attr('type', 'text');
		icon1.removeClass("fa-eye-slash");
		icon1.addClass("fa-eye");
	}
});


function checkUsernameExist(username) {
	$.ajax({
		url: '/cloud_instance/checkUsernameExist',
		data: {
			"username": username
		},
		success: function(result) {
			if (result == "duplicate") {
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Username already Exists',
					showConfirmButton: false,
					timer: 3000
				})
				//$('#adduserForm').prop( "disabled", true );
				$("#adduseridd").hide();
				usernameFlag = 1;
			}
			else {
				//$('#adduserForm').prop( "disabled", false );
				$("#adduseridd").show();
				usernameFlag = 0;
			}
		}
	});

}

function checkMobileNumber(mobileNo) {
	$.ajax({
		url: '/cloud_instance/checkMobileNumber',
		data: {
			"mobileNo": mobileNo
		},
		success: function(result) {
			if (result == "duplicate") {
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Mobile number already Exists',
					showConfirmButton: false,
					timer: 3000
				})
				//				$('#adduserForm').prop( "disabled", true );
				$("#adduseridd").hide();
				mobileFlag = 1;
			}
			else {
				//				$('#adduserForm').prop( "disabled", false );
				$("#adduseridd").show();
				mobileFlag = 0;
			}
		}
	});
}

function checkDuplicateEmail(email) {
	$.ajax({
		url: '/cloud_instance/checkDuplicateEmail',
		data: {
			"email": email
		},
		success: function(result) {
			if (result == "duplicate") {
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Email already Exists',
					showConfirmButton: false,
					timer: 3000
				})
				//				$('#adduserForm').prop( "disabled", true );
				$("#adduseridd").hide();
				emailFlag = 1;
			}
			else {
				//				$('#adduserForm').prop( "disabled", false );
				$("#adduseridd").show();
				emailFlag = 0;
			}
		}
	});
}


function roleChange(role) {
	var selectedRole = role;
	var $groupName = $('#appUser\\.groupName');

	if (selectedRole == '1' || selectedRole == '3') { // Example condition for Admin and Super Admin
		$groupName.prop('multiple', true).addClass('select2');
		$groupName.select2({
			placeholder: "Select Group",
			allowClear: true
		});
	} else {
		$groupName.prop('multiple', false).removeClass('select2').select2('destroy');
	}
}


function validateForm() {

	if (emailFlag == 1) {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Email already Exists',
			showConfirmButton: false,
			timer: 3000
		})
		return false;
	}
	if (mobileFlag == 1) {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Mobile already Exists',
			showConfirmButton: false,
			timer: 3000
		})
		return false;
	}

	if (usernameFlag == 1) {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Mobile number already Exists',
			showConfirmButton: false,
			timer: 3000
		})
		return false;
	}
	

}
