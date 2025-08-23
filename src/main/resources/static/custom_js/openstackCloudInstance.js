$('#createVMBtn').click(function() {

	$('#Openstackcreatevmspinner').show();

	$.ajax({
		url : '/openstack/createInstance',
		data : {
			"instanceName" : $('#instance_name').val(),
			"image" : $('#image').val(),
			"flavor" : $('#flavor').val(),
			"availabilityZone" : $('#availabilityZone').val(),
			"network" : $('#network').val(),
			"keyPair" : $('#keyPair').val(),
			"securityGroupName" : $('#securityGroupName').val(),
			"projectid" : $('#projectid').val(),

		},
		success : function(result) {
			$('#Openstackcreatevmspinner').hide();
			if (result == "success") {
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'VM created successfully on Openstack',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/openstack/new";
				})
			} else {

				Swal.fire({
					position : 'top',
					icon : 'warning',
					title : result,
					showConfirmButton : false,
					timer : 3000
				}).then(() => {
					window.location = "/openstack/new";
				})

			}
		}
	});
})

$('#DiscoverButtonClick').click(function() {

	$('#discoverSpinner').show();
	$.ajax({
		url : '/OpenStackDiscover/save',
		data : {
			"serverIP" : $('#serverIP').val(),
			"discoverType" : $('#Switch_VM').val()
		},
		success : function(result) {
			$('#discoverSpinner').hide();
			if (result == "success") {
				Swal.fire({
					position : 'top',
					icon : 'success',
					title : $('#Switch_VM').val() + " discovered successfully",
					showConfirmButton : false,
					timer : 3000
				})
			} else {

				Swal.fire({
					position : 'top',
					icon : 'warning',
					title : result,
					showConfirmButton : false,
					timer : 3000
				})

			}
		}
	});
})

// Openstack Super Admin Functionalities
$('#approvalAction').change(function() {
    var selectedValue = $(this).val();
    var requestId =  $(".approve-btn").attr("data-request-id");
    console.log("first "+requestId);
    $('#adminRequestIdField').val(requestId);
    if(selectedValue === 'Reject') {
    	$('#remark1').show();
    	$('#remark').show();
    	
    	$('#callVMrejectbysuperid').show();
    	
    	
    	$('#callVMApprovalbysuperid').hide();
    	
    	$('#zoneLabel').hide();
    	$('#availabilityZone').hide();
    	
    	$('#ProjectLabel').hide();
    	$('#ProjectZone').hide();
    	
    	$('#networkLabel').hide();
    	$('#network').hide();
    	
    	$('#keyPairLabel').hide();
    	$('#keyPair').hide();
    	
    	$('#securityGroupNameLabel').hide();
    	$('#securityGroupName').hide();
    } 
    else if(selectedValue === 'Approved'){
    	$('#remark1').hide();
    	$('#remark').hide();
    	

    $('#callVMApprovalbysuperid').show();
    	
    	$('#callVMrejectbysuperid').hide();
    	
    	$('#zoneLabel').show();
    	$('#availabilityZone').show();
    	

    	$('#ProjectLabel').show();
    	$('#ProjectZone').show();
    	
    	$('#networkLabel').show();
    	$('#network').show();
    	
    	$('#keyPairLabel').show();
    	$('#keyPair').show();
    	
    	$('#securityGroupNameLabel').show();
    	$('#securityGroupName').show();
    	
    }
    else{
    	$('#remark1').hide();
    	$('#remark').hide();
    	
    	$('#zoneLabel').hide();
    	$('#availabilityZone').hide();
    	
    	$('#networkLabel').hide();
    	$('#network').hide();
    	
    	$('#keyPairLabel').hide();
    	$('#keyPair').hide();
    	
    	$('#securityGroupNameLabel').hide();
    	$('#securityGroupName').hide();
    }
});
// End Openstack Super Admin Functionalities



// Openstack Admin Functionalities
$('.adminApprove-btn').click(function() {
    var requestId = $(this).data('request-id');
    $('#adminRequestIdField').val(requestId);
});

$('#adminApprovalAction').change(function() {
    var selectedValue = $(this).val();
    if(selectedValue === 'Reject') {
    	$('#adminRemark').show();
    	$('#adminTextArea').show();
    	
    } 
    else{
    	$('#adminRemark').hide();
    	$('#adminTextArea').hide();
    	
    }
});

function callAdminApproval()
{
	var action = $('#adminApprovalAction').val();
	var reqId =  $('#adminRequestIdField').val();
	if(action == "Approved"){
		acceptAdminApproval(reqId)
	}
	else if(action == "Reject")
	{
		var remark = $('#adminTextArea').val();
		if(remark == ''){
			Swal.fire({
				position: 'top',
				icon: 'warning',
				title: 'Please provide Remark',
				showConfirmButton: false,
				timer: 3000
			})
		}
		else{
			rejectAdminApproval(reqId, remark);
		}
		
	}
}

function acceptAdminApproval(reqId)
{
	$('#adminSpinner').show();
	$.ajax({
		url : '/openstack/acceptAdminApproval',
		data : {
			"reqId" : reqId
		},
		success : function(result) {
			$('#adminSpinner').hide();
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Admin approval successfull',
					showConfirmButton: true,
					timer: 3000,
				})
				.then(() => {
					window.location = "/openstack/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to approve request',
					showConfirmButton: false,
					timer: 3000
				})
				.then(() => {
					window.location = "/openstack/allRequests";
				})
			}
			
		}
		
	});
}

function rejectAdminApproval(reqId, remark)
{
	$('#adminSpinner').show();
	$.ajax({
		url : '/openstack/rejectAdminApproval',
		data : {
			"reqId" : reqId,
			"remark" : remark
		},
		success : function(result) {
			$('#adminSpinner').hide();
			if(result == "reject"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Admin approval rejected',
					showConfirmButton: true,
					timer: 3000,
				})
				.then(() => {
					window.location = "/openstack/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to reject request',
					showConfirmButton: false,
					timer: 3000
				})
				.then(() => {
					window.location = "/openstack/allRequests";
				})
			}
			
		}
		
	});
}

// End Openstack Admin Functionalities


// Openstack User Functionalities
$('#vmButton').click(function() {

	$('#vmSpinner').show();

	$.ajax({
		url : '/openstack_user/request',
		data : {
			"instanceName" : $('#instance_name').val(),
			"image" : $('#image').val(),
			"flavor" : $('#flavor').val(),
		},
		success : function(result) {
			$('#vmSpinner').hide();
			
			const arr = result.split("~");
			
			if (arr[0] == "success") {
				Swal.fire({
					position: "top",
					icon: "success",
					title: "Dear " + arr[1] +", your request is been raised for VM creation",
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/openstack_user/vm";
				})
			} else {

				Swal.fire({
					position : 'top',
					icon : 'Failed to raise request for VM creation',
					title : result,
					showConfirmButton : false,
					timer : 3000
				}).then(() => {
					window.location = "/openstack_user/vm";
				})

			}
		}
	});
})
// End Openstack user Functionalities



//Super admin functionalities
function callVMApprovalbysuper(){
	
	
	
	
	var reqId =  $('#adminRequestIdField').val();
	 console.log(reqId);

	$('#actionSpinnerhere2').show();

	$.ajax({
		url : '/openstack/AceeptedCreatingVmBySuper',
		data : {
			"availabilityZone" : $('#availabilityZone').val(),
			"network" : $('#network').val(),
			"keyPair" : $('#keyPair').val(),
			"securityGroupName" : $('#securityGroupName').val(),
			"requestId" :reqId,
			"Projectid" :$('#ProjectZone').val(),
		},
		success : function(result) {
			$('#actionSpinnerhere2').hide();
			
			
			
			if (result == "success") {
				Swal.fire({
					position: "top",
					icon: "success",
					title: "Dear " + arr[1] +", your Vm in created",
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/openstack/allRequests";
				})
			} else {

				Swal.fire({
					position : 'top',
					icon : 'Failed to Create VM',
					title : result,
					showConfirmButton : false,
					timer : 3000
				}).then(() => {
					window.location = "/openstack/allRequests";
				})

			}
		}
	});
	
	
	
}


function callVMrejectbysuper()
{
	//alert("In super reject")
	var remark = $('#remark').val();
	var reqId =  $('#adminRequestIdField').val();
	if(remark == ''){
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Please provide Remark',
			showConfirmButton: false,
			timer: 3000
		})
	}
	else{
		$('#actionSpinnerhere23').show();
		$.ajax({
			url : '/openstack/rejectSuperAdminApproval',
			data : {
				"reqId" : reqId,
				"remark" : remark
			},
			success : function(result) {
				$('#actionSpinnerhere23').hide();
				if(result == "reject"){
					Swal.fire({
						position: "top",
						icon: "success",
						title: 'Request rejected',
						showConfirmButton: true,
						timer: 3000,
					})
					.then(() => {
						window.location = "/openstack/allRequests";
					})
				}
				else{
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: 'Failed to reject request',
						showConfirmButton: false,
						timer: 3000
					})
					.then(() => {
						window.location = "/openstack/allRequests";
					})
				}
				
			}
			
		});
	}
}

$('#createProjectBtn').click(function() {

	$('#OpenstackcreateProjectspinner').show();

	$.ajax({
		url : '/openstack/createProject',
		data : {
			"createProjectname" : $('#create_Project_name').val(),
			"createProjectDescription" : $('#create_Project_Description').val(),
			"CreateProjectEnabled" : $('#Create_Project_Enabled').val(),
			

		},
		success : function(result) {
			$('#OpenstackcreateProjectspinner').hide();
			if (result == "success") {
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Project created successfully on Openstack',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/openstack/Projectview";
				})
			} else {

				Swal.fire({
					position : 'top',
					icon : 'warning',
					title : result,
					showConfirmButton : false,
					timer : 3000
				}).then(() => {
					window.location = "/openstack/Projectview";
				})

			}
		}
	});
})

