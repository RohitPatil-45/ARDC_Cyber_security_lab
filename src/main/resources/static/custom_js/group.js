function checkGroupExist(groupName)
{
	$.ajax({
		url : '/group/checkGroupExist',
		data : {
			"groupName" : groupName
		},
		success : function(result) {
			if(result == "duplicate")
			{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Group already Exists',
					showConfirmButton: false,
					timer: 3000
				})
				$('#groupSave').prop( "disabled", true );
			}
			else{
				$('#groupSave').prop( "disabled", false );
			}
		}
	});
}