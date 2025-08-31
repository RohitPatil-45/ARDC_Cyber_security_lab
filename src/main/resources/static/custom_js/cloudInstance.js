var booleandiscountcheck = false;
$('document')
	.ready(
		function() {

			// $('#discount').prop('disabled', true);

			$.ajax({
				url: '/cloud_instance/getVMLocationPath',
				data: {
					"location_id": "5"
				},
				success: function(result) {
					var result = JSON.parse(result);
					$('#vm_location_path').val(result[0][0]);
				}
			});

			$('.billingTab').click(function() {
				booleandiscountcheck = true;
				var discountType = $(this).text();
				$.ajax({
					url: '/cloud_instance/getDiscount',
					data: {
						"discountType": discountType
					},
					success: function(discountValue) {
						console.log(discountValue);
						let parts = discountValue.split(",");
						console.log(parseInt(parts[0]));
						console.log(parts[1]);
						$('#discount_id').val(parts[0]);
						$('#discount').val(parts[1]);
					}
				});

			});

			var password = document.getElementById("instance_password")

			//					$('#instance_password')
			//							.keyup(
			//									function() {
			//										var pwd = $(this).val();
			//										var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
			//										if (pattern.test(pwd)) {
			//											password.setCustomValidity('');
			//											// $('#createVMBtn').prop('disabled',
			//											// false);
			//										} else {
			//											password
			//													.setCustomValidity('Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.');
			//										}
			//
			//										// alert(booleandiscountcheck);
			//										if (booleandiscountcheck) {
			//											// alert('outside')
			//											// $('#tooltip2').hide();
			//											$('#createVMBtn').prop('disabled',
			//													false);
			//										} else {
			//
			//											// $('#tooltip2').show();
			//
			//											this
			//													.setCustomValidity('Please select Discount Above');
			//
			//										}
			//									});

			// $('#location_id').keyup(function() {
			// var location = $(this).val();
			// if (location != '') {
			// this.setCustomValidity('');
			// $('#createVMBtn').prop('disabled', false);
			// } else {
			// this.setCustomValidity('Please select Region');
			// }
			// });

			$('#subproduct_id')
				.keyup(
					function() {

						var sub = $(this).val();
						if (sub != '') {
							this.setCustomValidity('');
							$('#createVMBtn').prop('disabled',
								false);
						} else {
							this
								.setCustomValidity('Please select Sub Product');
						}
					});

			$('#discount').keyup(function() {
				var discount = $(this).val();
				// alert(discount);
				if (discount != '') {
					this.setCustomValidity('');
					$('#createVMBtn').prop('disabled', false);
				} else {
					this.setCustomValidity('Please select Discount');
				}
			});

			$('#security_group_id').keyup(function() {
				var firewall = $(this).val();
				if (firewall != '') {
					this.setCustomValidity('');
					$('#createVMBtn').prop('disabled', false);
				} else {
					this.setCustomValidity('Please select Firewall');
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

function checkforDuplicateVM(instanceName) {

	const regex = /^[a-zA-Z0-9]+$/;

	if (regex.test(instanceName)) {
		$.ajax({
			url: '/cloud_instance_user/checkforDuplicateVM',
			data: {
				"instanceName": instanceName
			},
			success: function(result) {
				if (result == "duplicate") {
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: 'VM already Exists',
						showConfirmButton: false,
						timer: 3000
					})
					$('#createVMBtn').attr("disabled", "disabled");
					$('#instance_name').val("");
				} else {
					$('#createVMBtn').removeAttr("disabled");
				}
			}
		});
	} else {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'VM name should contain only letters and numbers, without spaces or special characters !',
			showConfirmButton: false,
			timer: 5000
		})
		$('#createVMBtn').attr("disabled", "disabled");
		$('#instance_name').val("");
	}
}

function getSubProduct(product) {
	var s = "<option value=''>SELECT</option>";
	$.ajax({
		url: '/sub_product/getSubProductName',
		data: {
			"productName": product
		},
		success: function(result) {
			var result = JSON.parse(result);

			for (var i = 0; i < result.length; i++) {

				s += '<option value="' + result[i][0] + '">' + result[i][1]
					+ '</option>';
			}
			$('#subproduct_id').html(s);
		}
	});

	$('#subproduct_id').html(s);
}

/*
 * function getVPC() { var s = '<option value=' + -1 + '>SELECT</option>';
 * $.ajax({ url : '/vpc/getVPCNetworks', success : function(data) { var result =
 * JSON.parse(data);
 * 
 * for (var i = 0; i < result.length; i++) {
 * 
 * s += '<option value="' + result[i][0] + '">' + result[i][1] + '</option>'; }
 * $('#vpc').html(s); } });
 * 
 * $('#vpc').html(s); }
 */

function getFirewall() {
	var s = '<option value=' + -1 + '>SELECT</option>';
	$.ajax({
		url: '/firewall/getFirewall',
		success: function(result) {
			var result = JSON.parse(result);

			for (var i = 0; i < result.length; i++) {

				s += '<option value="' + result[i][0] + '">' + result[i][1]
					+ '</option>';
			}
			$('#firewall').html(s);
		}
	});

	$('#firewall').html(s);
}

function getVMLocationPath(locationID) {
	$.ajax({
		url: '/cloud_instance/getVMLocationPath',
		data: {
			"location_id": locationID
		},
		success: function(result) {
			var result = JSON.parse(result);
			$('#vm_location_path').val(result[0][0]);
		}
	});

}

function getSwitchByIP(serverIP) {
	var s = "<option value=''>SELECT</option>";
	$.ajax({
		url: '/approval/getSwitchByIP',
		data: {
			"serverIP": serverIP
		},
		success: function(result) {
			var result = JSON.parse(result);
			console.log(result)
			for (var i = 0; i < result.length; i++) {

				s += '<option value="' + result[i][1] + '">' + result[i][0]
					+ '</option>';
			}
			$('#switch_id').html(s);
		}

	});

	$('#switch_id').html(s);
}

// Get IP by Virtualization type
//function getIpByVirtualizationType(serverIP) {
//	var s = "<option value=''>SELECT</option>";
//	alert("serverIPType : " + serverIP)
//	var Type = serverIP;
//	if (Type === "Docker") {
//		$("#SwitchSeaction").hide();
//		alert("insidehide_serverIPType : " + serverIP)
//	} else {
//		$("#SwitchSeaction").show();
//	}
//	$.ajax({
//		url: '/approval/getIpByVirtualizationType',
//		data: {
//			"serverIP": serverIP
//		},
//		success: function(result) {
//			var result = JSON.parse(result);
//			console.log(result)
//			for (var i = 0; i < result.length; i++) {
//
//				s += '<option value="' + result[i][0] + '">' + result[i][0]
//					+ '</option>';
//			}
//			$('#physicalServerIP').html(s);
//		}
//
//	});
//
//	$('#physicalServerIP').html(s);
//}


function getIpByVirtualizationType(serverIP) {
	var s = "<option value='NONE'>SELECT</option>";
	console.log("serverIPType : " + serverIP);

	if (serverIP === "Docker") {
		$("#SwitchSeaction").hide();
		$("#switch_id").val("NONE"); // Set to default
		$("#switch_id").prop("required", false); // Not required
	} else {
		$("#SwitchSeaction").show();
		$("#switch_id").prop("required", true); // Required when visible
	}

	// Optional: fetch and populate another dropdown
	$.ajax({
		url: '/approval/getIpByVirtualizationType',
		data: {
			"serverIP": serverIP
		},
		success: function(result) {
			let parsed = JSON.parse(result);
			console.log(parsed);
			for (var i = 0; i < parsed.length; i++) {
				s += '<option value="' + parsed[i][0] + '">' + parsed[i][0] + '</option>';
			}
			$('#physicalServerIP').html(s);
		}
	});

	$('#physicalServerIP').html(s); // Optional fallback
}


function showDBForm() {
	$('#dbForm').show();
}

function hideDBForm() {
	$('#dbForm').hide();
}

function handleFormSubmit(event) {
	// Prevent multiple form submissions
	const submitButton = document.getElementById("createVMBtn");
	const spinner = document.getElementById("vmCreationSpinner");

	// Disable the button and show the spinner
	submitButton.disabled = true;
	spinner.style.display = "inline-block"; // Make spinner visible

	// Allow the form to submit naturally
}



function createDockerContainer(instanceID) {
	$('#createSpinner').show();
	$.ajax({
		type: "POST",
		url: '/cloud_instance/docker',
		data: {
			"instanceID": instanceID
		},
		success: function(result) {
			$('#createSpinner').hide();

			alert(result);
		}
	});
}
