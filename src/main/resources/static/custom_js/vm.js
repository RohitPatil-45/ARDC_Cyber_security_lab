window.onload = function(){
	
	$('#daterange-btn').daterangepicker(
			{
				timePicker : true,
				timePickerIncrement : 10,
				ranges : {
					'Today' : [ moment().hours(0).minutes(0).seconds(0),
							moment().hours(23).minutes(59).seconds(59) ],
					'Yesterday' : [
							moment().hours(0).minutes(0).seconds(0)
									.subtract(1, 'days'),
							moment().hours(23).minutes(59).seconds(59)
									.subtract(1, 'days') ],
					'Last 7 Days' : [
							moment().hours(0).minutes(0).seconds(0)
									.subtract(6, 'days'),
							moment().hours(23).minutes(59).seconds(59) ],
					'Last 30 Days' : [
							moment().hours(0).minutes(0).seconds(0)
									.subtract(29, 'days'),
							moment().hours(23).minutes(59).seconds(59) ],
					'This Month' : [ moment().startOf('month'),
							moment().endOf('month') ],
					'Last Month' : [
							moment().subtract(1, 'month').startOf('month'),
							moment().subtract(1, 'month').endOf('month') ],
				// 'Last 3 Month' : [
				// moment().subtract(4, 'month').startOf('month'),
				// moment().subtract(1, 'month').endOf('month') ],
				// 'Last 6 Month' : [
				// moment().subtract(7, 'month').startOf('month'),
				// moment().subtract(1, 'month').endOf('month') ],
				// 'Last Year' : [
				// moment().subtract(21, 'month').startOf('month'),
				// moment().subtract(10, 'month').endOf('month') ],
				},
				startDate : moment().subtract(29, 'days'),
				endDate : moment()
			}, function(start, end) {
				var from_date = document.getElementById("from_date");
				from_date.value = start.format('YYYY-MM-DD HH:mm:ss');
				var to_date = document.getElementById("to_date");
				to_date.value = end.format('YYYY-MM-DD HH:mm:ss');

			})
	
	$.ajax({
		url : '/cloud_instance/getCpuUtilization',
		data : {
			ip_address : $('#instance_vmname').val(),
			physicalServerIPtemp : $('#physicalServerIPtemp').val()
			
			
		
		},
		success : function(cpuUtilization) {

			Highcharts.stockChart('cpuUtilizationGraph', {
			    chart: {
			    	
			        events: {
			            load: function () {
			            	
			                // set up the updating of the chart each second
			                var series = this.series[0];
			                var series2 = this.series[1];
			                
			                
			                setInterval(function () {
			                	$.ajax({
			                		url : '/cloud_instance/getCpuUtilization',
			                		data : {
			                			ip_address : $('#instance_vmname').val(),
			                			physicalServerIPtemp : $('#physicalServerIPtemp').val()
			                			
			                		},
			                		success : function(cpuUtilization) {
			                	
			                			var x = (new Date()).getTime(), 
				                    		y=cpuUtilization,
				                    		z=20;
				                    
				                    series.addPoint([x, y], true, true);
				                    series2.addPoint([x, z], true, true);
			                			
			                		}
			                		
			                	});
			              
			                }, 20000);
			               
			            }
			        }
			    },

			    accessibility: {
			        enabled: false
			    },

			    time: {
			        useUTC: false
			    },

			    rangeSelector: {
			        buttons: [{
			            count: 1,
			            type: 'minute',
			            text: '1M'
			        }, {
			            count: 5,
			            type: 'minute',
			            text: '5M'
			        }, {
			            type: 'all',
			            text: 'All'
			        }],
			        inputEnabled: false,
			        selected: 0
			    },

			    
			    exporting: {
			        enabled: false
			    },

			    
			    series: [{
			        name: 'CPU Utilization(%)',
			        data: (function () {
			            // generate an array of random data
			            var data = [],
			                time = (new Date()).getTime(),
			                i;

			            for (i = -999; i <= 0; i += 1) {
			                data.push([
			                    time + i * 1000,
			                    cpuUtilization
			                ]);
			            }
			            return data;
			        }())
			    },
			    ]
			});
			

		}
	});	
}

function labelFormatter(label, series) {
	return '<div style="font-size:13px; text-align:center; padding:2px; color: #fff; font-weight: 600;">'
			+ label + '<br>' + Math.round(series.percent) + '%</div>'
}

function getSubProduct(product) {
	var s = '<option value=' + -1 + '>SELECT</option>';
		$.ajax({
			url : '/sub_product/getSubProductName',
			data : {
				"productName" : product
			},
			success : function(result) {
				var result = JSON.parse(result);
				console.log(result)
				for (var i = 0; i < result.length; i++) {
					
					s += '<option value="' + result[i][0] + '">' + result[i][1]
							+ '</option>';
				}
				$('#subproduct_id').html(s);
			}
		});
	
	$('#subproduct_id').html(s);
}

function addExternalStorage(instance_id, disk_path){
	// alert(instance_id +" - "+disk_path);
	
	if($('#storage_size').val() == ""){
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Please provide storage size',
			showConfirmButton: false,
			timer: 3000
		})
	}
	else{
		$.ajax({
			url : '/cloud_instance_user/additionalStorageRequest',
			data : {
				"instance_id" : instance_id,
				"disk_path" : disk_path,
				"disk_size" : $('#storage_size').val(),
			},
			success : function(result) {
				if(result == "true"){
					Swal.fire({
						position: "top",
						icon: "success",
						title: 'Your request is raise for external Storage',
						showConfirmButton: true,
						timer: 3000,
					}).then(() => {
						window.location = "/cloud_instance/VM/"+instance_id;
					})
				}
				else{
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: 'Failed to raise for external Storage',
						showConfirmButton: false,
						timer: 3000
					}).then(() => {
						window.location = "/cloud_instance/VM/"+instance_id;
					})
				}
			}
		});
	}
	
	
}

function externalStorageSuper(instance_id, disk_path){
	if($('#storage_size').val() == ""){
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Please provide storage size',
			showConfirmButton: false,
			timer: 3000
		})
	}
	else{
		$('#storageSpinner').show();
		$.ajax({
			url : '/cloud_instance/additionalStorageRequestSuper',
			data : {
				"instance_id" : instance_id,
				"disk_path" : disk_path,
				"disk_size" : $('#storage_size').val(),
			},
			success : function(result) {
				$('#storageSpinner').hide();
				if(result == "true"){
					Swal.fire({
						position: "top",
						icon: "success",
						title: 'Storage added to the current VM',
						showConfirmButton: true,
						timer: 3000,
					}).then(() => {
						window.location = "/cloud_instance/VM/"+instance_id;
					})
				}
				else{
					Swal.fire({
						position: 'top',
						icon: 'warning',
						title: 'Failed to add external Storage',
						showConfirmButton: false,
						timer: 3000
					}).then(() => {
						window.location = "/cloud_instance/VM/"+instance_id;
					})
				}
			}
		});
	}
}


function startVM(id){
	$('#startSpinner').show();
	window.location = "/cloud_instance/startVM/"+id;
}


function stopVM(id){
	$('#stopSpinner').show();
	window.location = "/cloud_instance/stopVM/"+id;
}

function changeVMState(state, instance_id){
	$('#activeSpinnner').show();
	$.ajax({
			url : '/cloud_instance/changeVMState',
			data : {
				"state" : state,
				"instance_id" : instance_id
			},
			success : function(result) {
				// alert("VM state change");
				$('#activeSpinnner').hide();
				window.location = "/cloud_instance/VM/"+instance_id;
				// $('.nav-link[href="#basicInfo"]').removeClass('active');
				// $('.nav-link[href="#start_stop"]').tab('show');
				
				
			}
		});
}

function vmDeletionRequest(instanceID){
	
	if($('#textVerification').val() == ""){
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Please provide below Text',
			showConfirmButton: false,
			timer: 3000
		})
	}
	else{
		if($('#textVerification').val() == "I am aware this action will delete data and server permanently"){
			
			$('#destorySpinner').show();
			
			$.ajax({
				url : '/cloud_instance/vmDeletionRequest',
				data : {
					"instance_id" : instanceID
				},
				success : function(result) {
					$('#destorySpinner').show();
					const arr = result.split("~");
					if(arr[0] == "success"){
						Swal.fire({
							position: "top",
							icon: "success",
							title: 'Dear '+arr[1]+', your request for VM deletion has been raised',
							showConfirmButton: true,
							timer: 3000,
						})
// .then(() => {
// window.location = "/cloud_instance/VM/"+instance_id;
// })
					}
					else{
						Swal.fire({
							position: 'top',
							icon: 'warning',
							title: 'Failed to raise request for VM deletion',
							showConfirmButton: false,
							timer: 3000
						})
// .then(() => {
// window.location = "/cloud_instance/VM/"+instance_id;
// })
					}
				}
			});
			// alert("instance id = "+instanceID);
		}
		else{
			Swal.fire({
				position: 'top',
				icon: 'warning',
				title: 'Please provide same text as given below',
				showConfirmButton: false,
				timer: 3000
			})
		}
		
	}
}


function vmDeletion(instanceID){
	if($('#textVerification').val() == ""){
		Swal.fire({
			position: 'top',
			icon: 'warning',
			title: 'Please provide below Text',
			showConfirmButton: false,
			timer: 3000
		})
	}
	else{
		if($('#textVerification').val() == "I am aware this action will delete data and server permanently"){
			$.ajax({
				url : '/cloud_instance/vmDeletionSuper',
				data : {
					"instance_id" : instanceID
				},
				success : function(result) {
					const arr = result.split("~");
					if(arr[0] == "success"){
						Swal.fire({
							position: "top",
							icon: "success",
							title: 'VM deleted successfully',
							showConfirmButton: true,
							timer: 3000,
						})
						.then(() => {
							window.location = "/cloud_instance/view";
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
							window.location = "/cloud_instance/view";
						})
					}
				}
			});
		}
		else{
			Swal.fire({
				position: 'top',
				icon: 'warning',
				title: 'Please provide same text as given below',
				showConfirmButton: false,
				timer: 3000
			})
		}
		
	}
}



function generateAllVmReports(vm)
{
		
		var fDate = $('#from_date').val();
		var toDate = $('#to_date').val();
		
		getVmUtilizationReport(fDate, toDate, vm);
}


function getVmUtilizationReport(fDate, toDate, vm)
{
	$.ajax({
		url : '/report/vmUtilizationReport',
		data : {
			"fromDate" : fDate,
			"toDate" : toDate,
			"vm" : vm
		},

		success : function(data2) {
			
			var data = JSON.parse(data2);
			
			$('#vmUtilizationDiv1').show();
			
// console.log(data);
			
			 if ($.fn.DataTable.isDataTable("#example1reporttable")) {
		            $('#example1reporttable').DataTable().clear().destroy();
		        }
			
			$("#example1reporttable").DataTable({

				"responsive" : false,
				"paging": true,
			    "pageLength": 10,
			    "data" : data[0],
				// "sScrollX" : "100%",
				// "sScrollXInner" : "200%",
				// "bScrollCollapse" : true,
				"lengthChange" : false,
				"autoWidth" : false,
				"buttons" : [ "csv", "excel", "pdf", "print", "colvis" ]
			}).buttons().container().appendTo(
					'#example1reporttable_wrapper .col-md-6:eq(0)');
			
			
			
			if ($.fn.DataTable.isDataTable("#vmHealthTable")) {
				            $('#vmHealthTable').DataTable().clear().destroy();
				        }
			
			$("#vmHealthTable").DataTable({

						"responsive" : false,
						"paging": true,
					    "pageLength": 10,
					    "data" : data[1],
						// "sScrollX" : "100%",
						// "sScrollXInner" : "200%",
						// "bScrollCollapse" : true,
						"lengthChange" : false,
						"autoWidth" : false,
						"buttons" : [ "csv", "excel", "pdf", "print", "colvis" ]
					}).buttons().container().appendTo(
							'#vmHealthTable_wrapper .col-md-6:eq(0)');
							

		if ($.fn.DataTable.isDataTable("#vmAvailabilityTable")) {
				            $('#vmAvailabilityTable').DataTable().clear().destroy();
				        }

			$("#vmAvailabilityTable").DataTable({

						"responsive" : false,
						"paging": true,
					    "pageLength": 10,
					    "data" : data[2],
						// "sScrollX" : "100%",
						// "sScrollXInner" : "200%",
						// "bScrollCollapse" : true,
						"lengthChange" : false,
						"autoWidth" : false,
						"buttons" : [ "csv", "excel", "pdf", "print", "colvis" ]
					}).buttons().container().appendTo(
							'#vmAvailabilityTable_wrapper .col-md-6:eq(0)');


		if ($.fn.DataTable.isDataTable("#vmCpuThresholdTable")) {
				            $('#vmCpuThresholdTable').DataTable().clear().destroy();
				        }


			$("#vmCpuThresholdTable").DataTable({

						"responsive" : false,
						"paging": true,
					    "pageLength": 10,
					    "data" : data[3],
						// "sScrollX" : "100%",
						// "sScrollXInner" : "200%",
						// "bScrollCollapse" : true,
						"lengthChange" : false,
						"autoWidth" : false,
						"buttons" : [ "csv", "excel", "pdf", "print", "colvis" ]
					}).buttons().container().appendTo(
							'#vmCpuThresholdTable_wrapper .col-md-6:eq(0)');


		if ($.fn.DataTable.isDataTable(vmMemoryThresholdTable)) {
				            $('#vmMemoryThresholdTable').DataTable().clear().destroy();
				        }

			$("#vmMemoryThresholdTable").DataTable({

						"responsive" : false,
						"paging": true,
					    "pageLength": 10,
					    "data" : data[4],
						// "sScrollX" : "100%",
						// "sScrollXInner" : "200%",
						// "bScrollCollapse" : true,
						"lengthChange" : false,
						"autoWidth" : false,
						"buttons" : [ "csv", "excel", "pdf", "print", "colvis" ]
					}).buttons().container().appendTo(
							'#vmMemoryThresholdTable_wrapper .col-md-6:eq(0)');					
			
		}
	});
}

function showRamDropdown(ramCheckbox) {
    if (ramCheckbox.checked) {
    	$('#ramDiv').show();
    }
    
    else{
    	$('#ramDiv').hide();
    }
}

function showCpuDropdown(cpuCheckbox) {
    if (cpuCheckbox.checked) {
    	$('#cpuDiv').show();
    }
    
    else{
    	$('#cpuDiv').hide();
    }
}

function showStorage(storageCheckbox) {
    if (storageCheckbox.checked) {
    	$('#storageDiv').show();
    }
    
    else{
    	$('#storageDiv').hide();
    }
}

function validateForm(event) {
    // Get elements
    const ramCheck = document.getElementById('ramCheck');
    const cpuCheck = document.getElementById('cpuCheck');
    //const storageCheck = document.getElementById('storageCheck');
    
    if (!ramCheck.checked && !cpuCheck.checked ) {
        alert('Please select at least one option (RAM, CPU).');
        return false; // Prevent form submission
    }

    // Validate RAM selection
    if (ramCheck.checked && ($('#newRAM').val() === '')) {
        alert('Please select a valid RAM option.');
        return false; // Prevent form submission
    }

    // Validate CPU selection
    if (cpuCheck.checked && $('#newCpu').val() === '') {
        alert('Please select a valid CPU option.');
        return false; // Prevent form submission
    }

    // Validate Storage size
    /*if (storageCheck.checked) {
        if ($('#storage_size').val() === '') {
            alert('Please enter a valid numeric value for storage size in GB.');
            return false; // Prevent form submission
        }
    }
*/
    // If all validations pass
    return true; // Allow form submission
}
