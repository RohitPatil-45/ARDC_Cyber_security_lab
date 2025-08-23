$('.approve-btn').click(function() {
    var requestId = $(this).data('request-id');
    $('#requestIdField').val(requestId);
    $('#instID').val(requestId);
    // alert(requestId)
});


$('.update-btn').click(function() {
    var requestId = $(this).data('request-id');
    $('#instID1').val(requestId);
});

$('.addStorage-btn').click(function() {
    var requestId = $(this).data('request-id');
    $('#adsInst').val(requestId);
});

function getSwitchByIP(serverIP){
	var s = "<option value=''>SELECT</option>";
	$.ajax({
		url : '/approval/getSwitchByIP',
		data : {
			"serverIP" : serverIP
		},
		success : function(result) {
			var result = JSON.parse(result);
			console.log(result)
			for (var i = 0; i < result.length; i++) {
				
				s += '<option value="' + result[i][0] + '">' + result[i][0]
						+ '</option>';
			}
			$('#switch').html(s);
		}
		
	});
	
	$('#switch').html(s);
}

$('.showDetails').click(function() {
	
    var logId = $(this).data('log-id');
    var instanceName = $(this).data('instance-name');
    var price = $(this).data('vcpu') +" / " + $(this).data('ram') + " / " +$(this).data('ssd-disk');
    var createdOn = $(this).data('created-on');
    var os = $(this).data('os');
    var switchName = $(this).data('switch');
    var location = $(this).data('location');
    var oldData = $(this).data('olddata') || "NA";
    var newData = $(this).data('newdata') || "NA";
    var ram2 = $(this).data('ram') || "NA";
    var cpu2 = $(this).data('vcpu') || "NA";
    var disk2 = $(this).data('ssd-disk') || "NA";
    
    $('#instanceName1').text(instanceName);
   // $('#instanceName2').text(instanceName);
   // $('#price').text(price);
    $('#ram2').text(ram2);
    $('#cpu2').text(cpu2);
    $('#disk2').text(disk2);
    $('#old').text(oldData);
    $('#new').text(newData);
    $('#createdOn').text(createdOn);
    $('#os').text(os);
    $('#switch').text(switchName);
    $('#location').text(location);
    
    
});

$('#approvalAction').change(function() {
    var selectedValue = $(this).val();
    if(selectedValue === 'Reject') {
    	$('#remark1').show();
    	$('#remark').show();
    	
    	$('#ipLabel').hide();
    	$('#physical_server_ip').hide();
    	
    	$('#switchLabel').hide();
    	$('#switch').hide();
    	$('#Virtualizationtypelabel').hide();
    	$('#VirtualizationtyprSelect').hide();
    	
    } 
    else{
// $('#remark1').hide();
// $('#remark').hide();
//    	
// $('#ipLabel').show();
// $('#physical_server_ip').show();
//    	
// $('#switchLabel').show();
// $('#switch').show();
//    	
 $('#Virtualizationtypelabel').show();
 $('#VirtualizationtyprSelect').show();
 
 $('#remark1').hide();
	$('#remark').hide();
    	
    }
});



function filterPhysicalServerIP() {
    const selectedType = document.getElementById('VirtualizationtyprSelect').value;
    const ipLabel = document.getElementById('ipLabel');
    const physicalServerSelect = document.getElementById('physical_server_ip');
    
    if ( selectedType === 'KVM'){
    	$('#remark1').hide();
    	$('#remark').hide();
    	
    	$('#ipLabel').show();
    	$('#physical_server_ip').show();
    	

    	$('#switchLabel').hide();
    	$('#switch').hide();
    	
    	$('#Virtualizationtypelabel').show();
    	$('#VirtualizationtyprSelect').show();
    	
    	
    }else{
    	$('#remark1').hide();
    	$('#remark').hide();
    	
    	$('#ipLabel').show();
    	$('#physical_server_ip').show();
    	
    	$('#switchLabel').show();
    	$('#switch').show();
    	
    	$('#Virtualizationtypelabel').show();
    	$('#VirtualizationtyprSelect').show();
    	
    }

    if (selectedType === 'hyperv' || selectedType === 'KVM') {
        ipLabel.style.display = 'block';
        physicalServerSelect.style.display = 'block';
        
        // Filter options based on virtualization type
        [...physicalServerSelect.options].forEach(option => {
            if (option.getAttribute('data-virtualization') === selectedType || option.value === '') {
                option.style.display = 'block';
            } else {
                option.style.display = 'none';
            }
        });
    } else {
        ipLabel.style.display = 'none';
        physicalServerSelect.style.display = 'none';
    }
}

function callVMApproval(){
	let isFalse = false;
	var instanceId =  $('#requestIdField').val();
	var action = $('#approvalAction').val();
	var serverIP = $('#physical_server_ip').val();
	var switchName = $('#switch').val();
	var virtulizationtype = $('#VirtualizationtyprSelect').val();
	
	if(action == "Approved"){
		
		
		if(virtulizationtype == "KVM"){
			
			
			$('#physical_server_ip').attr('required', true);
			
	// alert(serverIP);
	// alert(switchName);
	 if (serverIP === ''|| serverIP === null) {
		 isFalse = false;
	 alert("Please select a valid option in IP");
	 
	// $('#switch').val().tooltip('show');
	 }else{
		 isFalse = true;
	 }
	        
	
	        if(isFalse){
	        	approveVMCreation(instanceId, serverIP, switchName,virtulizationtype);
	        }
			
			
		}else{
			
			
			$('#switch').attr('required', true);
			$('#physical_server_ip').attr('required', true);
			
	// alert(serverIP);
	// alert(switchName);
	 if (serverIP === ''|| serverIP === null) {
		 isFalse = false;
	 alert("Please select a valid option in IP");
	 
	// $('#switch').val().tooltip('show');
	 }else{
		 isFalse = true;
	 }
	        
			
	 if (switchName === ''|| switchName === null) {
		 isFalse = false;
	 alert("Please select a valid option in switch");
	// $('#physical_server_ip').tooltip('show');
	 
	 }else{
		 isFalse = true;
	 }
	        if(isFalse){
	        	approveVMCreation(instanceId, serverIP, switchName,virtulizationtype);
	        }
			
		}
		
		
		
	}
	else if(action == "Reject"){
		
		var remark = $('#remark').val();
		
		rejectVMCreation(instanceId, remark);
	}
}

function approveVMCreation(instance_id, serverIP, switchName,virtulizationtype){
	
	if(virtulizationtype == "KVM"){
		
// alert("KVM selected")
		

		
		$('#actionSpinner').show();
		$.ajax({
			url : '/approval/requestApprovedforkvm',
			data : {
				"request_id" : instance_id,
				"serverIP" : serverIP,
				
			},
			success : function(result) {
				alert(result);
				$('#actionSpinner').hide();
				if(result == "success"){
					Swal.fire({
						position: "top",
						icon: "success",
						title: 'Approval for VM Creation is successful-KVM',
						showConfirmButton: true
						//timer: 10000,
					}).then(() => {
						window.location = "/approval/allRequests";
					})
				}
				else{
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: result,
						showConfirmButton: true
						//timer: 3000
					}).then(() => {
						window.location = "/approval/allRequests";
					})
				}
				
			}
			
		});
		
	}else{
		
		$('#actionSpinner').show();
		$.ajax({
			url : '/approval/requestApproved',
			data : {
				"request_id" : instance_id,
				"serverIP" : serverIP,
				"switchName" : switchName
			},
			success : function(result) {
				$('#actionSpinner').hide();
				if(result == "true"){
					Swal.fire({
						position: "top",
						icon: "success",
						title: 'Approval for VM Creation is successful-Hyper-V',
						showConfirmButton: true
						
					}).then(() => {
						window.location = "/approval/allRequests";
					})
				}
				else{
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: 'Something wrong happened',
						showConfirmButton: true
						
					}).then(() => {
						window.location = "/approval/allRequests";
					})
				}
				
			}
			
		});
		
	}
	
	

}

function rejectVMCreation(instance_id, remark){
	$('#actionSpinner').show();
	$.ajax({
		url : '/approval/requestRejected',
		data : {
			"request_id" : instance_id,
			"remark" : remark
		},
		success : function(result) {
			$('#actionSpinner').hide();
			if(result == "reject"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Request for VM creation is rejected',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Something wrong happened',
					showConfirmButton: false,
					timer: 3000
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
	
}

function showVMData(log_id){
	$.ajax({
		url : '/approval/currenVM',
		data : {
			"log_id" : log_id
		},
		success : function(result) {
			alert(result)
		}
		
	});
}

$('#vmDeleteAction').change(function() {
    var selectedValue = $(this).val();
    if(selectedValue === 'Reject') {
    	$('#remark12').show();
    	$('#delRemark').show();
    	
    } 
    else{
    	$('#remark12').hide();
    	$('#delRemark').hide();
    }
});

// Vm Deletion Requests


$('#vmUpdateAction').change(function() {
    var selectedValue = $(this).val();
    if(selectedValue === 'Reject') {
    	$('#remark123').show();
    	$('#updateRemark1').show();
    	
    } 
    else{
    	$('#remark123').hide();
    	$('#updateRemark1').hide();
    }
});


$('#vmAdsAction').change(function() {
    var selectedValue = $(this).val();
    if(selectedValue === 'Reject') {
    	$('#adsRemark').show();
    	$('#adsRemark1').show();
    	
    } 
    else{
    	$('#adsRemark').hide();
    	$('#adsRemark1').hide();
    }
});

function deleteVMApproval(){
	let isFalse = false;
	var reqID =  $('#instID').val();
	var action = $('#vmDeleteAction').val();
	if(action == "Approved"){
		approveVMDeletion(reqID)
	}
	else{
		var remark = $('#delRemark').val();
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
			rejectVMDeletion(reqID, remark);
		}
		
	}
}

function approveVMDeletion(requestId)
{
	$('#delSpinner').show();
	$.ajax({
		url : '/approval/approveVMDeletion',
		data : {
			"requestId" : requestId
		},
		success : function(result) {
			$('#delSpinner').hide();
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'VM is deleted successfully',
					showConfirmButton: true,
					timer: 3000,
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to delete VM',
					showConfirmButton: false,
					timer: 3000
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});

}


function rejectVMDeletion(reqID, remark){
	$('#delSpinner').show();
	$.ajax({
		url : '/approval/requestRejected',
		data : {
			"request_id" : reqID,
			"remark" : remark
		},
		success : function(result) {
			$('#delSpinner').hide();
			if(result == "reject"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Request for VM deletion is rejected',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to reject request',
					showConfirmButton: false,
					timer: 3000
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}
// End VM Deletion Request

// Update VM approval
function updateVMApproval()
{
	let isFalse = false;
	var instanceId =  $('#instID1').val(); //store request id
	var action = $('#vmUpdateAction').val();
	if(action == "Approved"){
		approveVMUpdate(instanceId)
	}
	else{
		var remark = $('#updateRemark1').val();
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
			rejectVMUpdate(instanceId, remark);
		}
		
	}
}

function approveVMUpdate(instanceId){
	$('#upSpinner').show();
	$.ajax({
		url : '/approval/approveVMUpdate',
		data : {
			"requestId" : instanceId
		},
		success : function(result) {
			alert(result)
			$('#upSpinner').hide();
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Resize VM successfully',
					showConfirmButton: true
					
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: result,
					showConfirmButton: true
					
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}

function rejectVMUpdate(instanceId, remark){
	$('#upSpinner').show();
	$.ajax({
		url : '/approval/requestRejected',
		data : {
			"request_id" : instanceId,
			"remark" : remark
		},
		success : function(result) {
			$('#upSpinner').hide();
			if(result == "reject"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Request for resizing VM is rejected',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to reject request',
					showConfirmButton: false,
					timer: 3000
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}
// End Update VM approval

// Additional Storage Functionality
function addAdditionalStorage()
{
	var instanceId =  $('#adsInst').val();
	var action = $('#vmAdsAction').val();
	if(action == "Approved"){
		approveAdditionalStorageRequest(instanceId)
	}
	else{
		var remark = $('#adsRemark1').val();
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
			rejectAdditionalStorageRequest(instanceId, remark);
		}
		
	}
}

function approveAdditionalStorageRequest(instanceId){
	$('#adsSpinner').show();
	$.ajax({
		url : '/approval/approveAdditionalStorageRequest',
		data : {
			"requestId" : instanceId
		},
		success : function(result) {
			$('#adsSpinner').hide();
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Storage added to the current VM request',
					showConfirmButton: true,
					timer: 3000,
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to add storage',
					showConfirmButton: false,
					timer: 3000
				})
				.then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}


function rejectAdditionalStorageRequest(instanceID, remark)
{
	$('#adsSpinner').show();
	$.ajax({
		url : '/approval/rejectAdditionalStorageRequest',
		data : {
			"request_id" : instanceID,
			"remark" : remark
		},
		success : function(result) {
			$('#adsSpinner').hide();
			if(result == "reject"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Request for additional storage is rejected',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to reject request',
					showConfirmButton: false,
					timer: 3000
				}).then(() => {
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}

// End Additional Storage Functionality


// Admin Approval
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
		url : '/approval/acceptAdminApproval',
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
					window.location = "/approval/allRequests";
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
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}

function rejectAdminApproval(reqId, remark)
{
	$('#adminSpinner').show();
	$.ajax({
		url : '/approval/rejectAdminApproval',
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
					window.location = "/approval/allRequests";
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
					window.location = "/approval/allRequests";
				})
			}
			
		}
		
	});
}

// End Admin Approval










