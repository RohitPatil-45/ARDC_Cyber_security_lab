$(function() {
	$.validator.setDefaults({
		submitHandler : function() {
			// alert( "Form successful submitted!" +$('#ip_address').val());

			generateVMActivityReport();

		}
	});
	$('#vmActivityReport').validate({
		rules : {

			from_date : {
				required : true
			},

			to_date : {
				required : true

			},

		},
		messages : {
			from_date : {
				required : "Please select ",

			},
			to_date : {
				required : "date range picker",
			}

		},
		errorElement : 'span',
		errorPlacement : function(error, element) {
			error.addClass('invalid-feedback');
			element.closest('.form-group').append(error);
		},
		highlight : function(element, errorClass, validClass) {
			$(element).addClass('is-invalid');
		},
		unhighlight : function(element, errorClass, validClass) {
			$(element).removeClass('is-invalid');
		}
	});
});

function generateVMActivityReport() {
	var fdate = $('#from_date').val();
	var tdate = $('#to_date').val();
	var instanceName = $('#instance_name').val();
	$.ajax({
		url : '/report/getVMActivityReport',
		type: 'GET',
		data : {
			"from_date" : fdate,
			"to_date" : tdate,
			"instance_name" : instanceName
		}
//		success : function(result) {
//			alert(result)
////			$('#tableCard').show();
////			$('#reportForm').hide();
//		}
	});
}