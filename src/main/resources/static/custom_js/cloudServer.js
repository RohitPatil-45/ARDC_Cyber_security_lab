window.addEventListener('click', function(e) {
	var menu = document.getElementById('contextMenu');
	menu.classList.remove('visible');
});
function showContextMenu(e) {
	var pulse = document.getElementById('pulse');
	var menu = document.getElementById('contextMenu');

	if (e.clientX > window.innerWidth - 220) {
		menu.style.left = (e.clientX - 220) + 'px';
		menu.style.transformOrigin = 'top right';
	} else {
		menu.style.left = e.clientX + 'px';
	}
	pulse.style.left = e.clientX - 10 + 'px';
	menu.style.top = e.clientY + 'px';
	pulse.style.top = (e.clientY - 10) + 'px';
	pulse.classList.add('active');
	setTimeout(function() {
		document.getElementById('pulse').classList.remove('active');
	}, 300);
	document.getElementById('contextMenu')
	menu.classList.add('visible');
	menu.style.transformOrigin = 'top left';
	return false;
}

function viewCloudServerDetails(instanceID) {
	//alert(instanceID)
	window.location = "/cloud_instance/VM/"+instanceID;
}


$("#addVMtoGroupBtn").click(function() {
	var instanceID = [];
	var table = $("#VmTable").DataTable();
	var s = table.column(0).checkboxes.selected();
	$.each(s, function(key, i) {
		instanceID.push(i);
	});
	
	if (instanceID.length === 0) {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Select atleast one VM',
			showConfirmButton: false,
			timer: 3000
		})
		
		return;
	}
	
	if ($('#groupName').val() == '') {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Select Group',
			showConfirmButton: false,
			timer: 3000
		})
		
		return;
	}
	
	$.ajax({
		type: "POST",
		url : '/cloud_instance/addVmToGroup',
		data : {
			"instanceID" : instanceID.toString(),
			"groupName" : $('#groupName').val()
		},
		success : function(result) {
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'VM added to Group',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/cloud_instance/view";
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
					window.location = "/cloud_instance/view";
				})
			}
			
		}
		
	});
})

$("#addCustomerNameToVM").click(function() {
	var instanceID = [];
	var table = $("#VmTable").DataTable();
	var s = table.column(0).checkboxes.selected();
	$.each(s, function(key, i) {
		instanceID.push(i);
	});
	
	if (instanceID.length === 0) {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Select atleast one VM',
			showConfirmButton: false,
			timer: 3000
		})
		
		return;
	}
	
	if ($('#customerName').val() == '') {
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Select Customer',
			showConfirmButton: false,
			timer: 3000
		})
		
		return;
	}
	
	$.ajax({
		type: "POST",
		url : '/cloud_instance/addCustomerNameToVM',
		data : {
			"instanceID" : instanceID.toString(),
			"customerName" : $('#customerName').val()
		},
		success : function(result) {
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'VM added to Customer',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/cloud_instance/view";
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
					window.location = "/cloud_instance/view";
				})
			}
			
		}
		
	});
})


function importSourceImage(templateId)
{
	$("#pageLoader").show();
	$.ajax({
		type: "POST",
		url : '/cloud_instance/sourceImage',
		data : {
			"templateId" :templateId
		},
		success : function(result) {
			$("#pageLoader").hide();
			if(result == "success"){
				Swal.fire({
					position: "top",
					icon: "success",
					title: 'Source Image created successfully',
					showConfirmButton: true,
					timer: 3000,
				}).then(() => {
					window.location = "/cloud_instance/view";
				})
			}
			else{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Failed to load Source image',
					showConfirmButton: false,
					timer: 3000
				}).then(() => {
					window.location = "/cloud_instance/view";
				})
			}
		}
	});
}