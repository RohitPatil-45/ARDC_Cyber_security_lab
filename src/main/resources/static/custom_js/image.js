function checkDuplicateImage(image){
	$.ajax({
		url : '/image/checkImageExist',
		data : {
			"image" : image
		},
		success : function(result) {
			if(result == "duplicate")
			{
				Swal.fire({
					position: 'top',
					icon: 'warning',
					title: 'Image already Exists',
					showConfirmButton: false,
					timer: 3000
				})
				$('#createImage').prop( "disabled", true );
			}
			else{
				$('#createImage').prop( "disabled", false );
			}
		}
	});
}