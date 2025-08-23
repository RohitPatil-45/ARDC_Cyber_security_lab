$('#SwitchSave').click(function(){
	
	$('#discoverSpinner').show();
	$.ajax({
		url : '/discover/save',
		data : {
			"serverIP" : $('#serverIP').val(),
			"discoverType" : $('#Switch_VM').val()
		},
		success : function(result) {
			$('#discoverSpinner').hide();
			if(result == "success"){
				Swal.fire({
					position: 'top',
					icon: 'success',
					title:  $('#Switch_VM').val() + " discovered successfully",
					showConfirmButton: false,
					timer: 3000
				})
			}
			else{
				
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title:  result,
					showConfirmButton: false,
					timer: 3000
				})
				
			}
		}
	});
})