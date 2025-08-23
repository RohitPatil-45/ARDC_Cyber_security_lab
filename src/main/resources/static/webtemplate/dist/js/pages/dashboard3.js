/* global Chart:false */

$(function() {
	//var ip = "192.168.0.230";
	var obj = document.getElementById("deviceIP");	
	var ip = obj.value;
//	alert(ip)
//	var l = window.location;
//	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
//	var serviceUrl = base_url + "/nodeDashboard/nodeLatencyStatusDetails";
//	$.ajax({
//		type : 'GET',
//		url : serviceUrl,
//		data : 'ip_address=' + ip,
//		dataType : 'json',
//		success : function(data) {
//			alert(data[0]["latency"])
//			alert(data[0]["packetLoss"])
//			Highcharts.setOptions({
//				global : {
//					useUTC : false
//				}
//			});
//			// Create the chart
//			Highcharts.stockChart('container_latency', {
//				chart : {
//					events : {
//						load : function() {
//							// set up the updating of the chart each second
//							// var series = this.series[0];
//							// var series2 = this.series[1];
//
//							var series = data[0]["latency"];
//							var series2 = data[0]["packetLoss"];
//							
//							// setInterval(function() {
//							// $.ajax({
//							// type: 'POST',
//							// url: 'latency_live_graph',
//							// data: 'device_ip=' + $('#ip_add').val()
//							// ,
//							// success: function(data)
//							// {
//							// var userData = data.split(",");
//							// var out_traffic = userData[0];
//							// var in_traffic = userData[1];
//							// var xxx = Number(out_traffic)
//							// var xxx2 = Number(in_traffic)
//							// var x = (new Date()).getTime(), // current time
//							// y = xxx;
//							// series.addPoint([x, y], true, true);
//							// series2.addPoint([x, xxx2], true, true);
//							// }
//							// });
//							//
//							//
//							// }, 6000);
//						}
//					}
//				},
//				rangeSelector : {
//					buttons : [ {
//						count : 1,
//						type : 'minute',
//						text : '1M'
//					}, {
//						count : 5,
//						type : 'minute',
//						text : '5M'
//					}, {
//						type : 'all',
//						text : 'All'
//					} ],
//					inputEnabled : false,
//					selected : 0
//				},
//				title : {
//					text : ''
//				},
//				exporting : {
//					enabled : false
//				},
//				series : [ {
//					name : 'Latency(m/s)',
//					data : (function() {
//						// generate an array of random data
//						var data = [], time = (new Date()).getTime(), i;
//						for (i = -999; i <= 0; i += 1) {
//							data.push([ time + i * 1000, Math.round(0)
//
//							// 10
//							]);
//						}
//
//						return data;
//					}())
//				}, {
//					name : 'Packet Drop(%)',
//					color : 'red',
//					data : (function() {
//						// generate an array of random data
//						var data = [], time = (new Date()).getTime(), i;
//						for (i = -999; i <= 0; i += 1) {
//							data.push([ time + i * 1000, Math.round(0)
//							// 20
//							]);
//						}
//
//						return data;
//					}())
//
//				}, ]
//			});
//			// var userData = data.split(",");
//			// var out_traffic = userData[0];
//			// var in_traffic = userData[1];
//			// var xxx = Number(out_traffic)
//			// var xxx2 = Number(in_traffic)
//			// var x = (new Date()).getTime(), // current time
//			// y = xxx;
//			// series.addPoint([x, y], true, true);
//			// series2.addPoint([x, xxx2], true, true);
//		}
//	});

	// alert("fygdy")
//	'use strict'
	var l = window.location;
	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
	var serviceUrl = base_url + "/nodeDashboard/nodeUpTimeStatusDetails";
	$.ajax({
		type : 'GET',
		url : serviceUrl,
		data : 'ip_address=' + ip,
		dataType : 'json',
		success : function(data) {
			var ticksStyle = {
				fontColor : '#495057',
				fontStyle : 'bold'
			}

			var mode = 'index'
			var intersect = true

			var $salesChart = $('#sales-chart')
			// eslint-disable-next-line no-unused-vars
			var salesChart = new Chart($salesChart, {
				type : 'bar',
				data : {
					labels : data[0]["catArray"],
					// labels: ['Feb 2022', 'Jan 2022', 'Dec 2021', 'Nov 2021',
					// 'Oct 2021', 'Sep 2021', 'Aug 2021'],
					datasets : [ {
						backgroundColor : '#007bff',
						borderColor : '#007bff',
						// data: [80, 35, 71, 90,45, 5, 60]
						data : data[0]["upArray"],
					}, {
						backgroundColor : '#ced4da',
						borderColor : '#ced4da',
						// data: [20, 65, 29, 10, 55, 91, 40]
						data : data[0]["downArray"],
					} ]
				},
				options : {
					maintainAspectRatio : false,
					tooltips : {
						mode : mode,
						intersect : intersect
					},
					hover : {
						mode : mode,
						intersect : intersect
					},
					legend : {
						display : false
					},
					scales : {
						yAxes : [ {
							// display: false,
							gridLines : {
								display : true,
								lineWidth : '4px',
								color : 'rgba(0, 0, 0, .2)',
								zeroLineColor : 'transparent'
							},
							ticks : $.extend({
								beginAtZero : true,

								// Include a dollar sign in the ticks
								callback : function(value) {
									if (value >= 1000) {
										value /= 1000
										value += 'k'
									}

									return '' + value
								}
							}, ticksStyle)
						} ],
						xAxes : [ {
							display : true,
							gridLines : {
								display : false
							},
							ticks : ticksStyle
						} ]
					}
				}
			})
		}
	})

//	var $visitorsChart = $('#visitors-chart')
//	// eslint-disable-next-line no-unused-vars
//	var visitorsChart = new Chart($visitorsChart,
//			{
//				data : {
//					labels : [ '18th', '20th', '22nd', '24th', '26th', '28th',
//							'30th' ],
//					datasets : [ {
//						type : 'line',
//						data : [ 100, 120, 170, 167, 180, 177, 160 ],
//						backgroundColor : 'transparent',
//						borderColor : '#007bff',
//						pointBorderColor : '#007bff',
//						pointBackgroundColor : '#007bff',
//						fill : false
//					// pointHoverBackgroundColor: '#007bff',
//					// pointHoverBorderColor : '#007bff'
//					}, {
//						type : 'line',
//						data : [ 60, 80, 70, 67, 80, 77, 100 ],
//						backgroundColor : 'tansparent',
//						borderColor : '#ced4da',
//						pointBorderColor : '#ced4da',
//						pointBackgroundColor : '#ced4da',
//						fill : false
//					// pointHoverBackgroundColor: '#ced4da',
//					// pointHoverBorderColor : '#ced4da'
//					} ]
//				},
//				options : {
//					maintainAspectRatio : false,
//					tooltips : {
//						mode : mode,
//						intersect : intersect
//					},
//					hover : {
//						mode : mode,
//						intersect : intersect
//					},
//					legend : {
//						display : false
//					},
//					scales : {
//						yAxes : [ {
//							// display: false,
//							gridLines : {
//								display : true,
//								lineWidth : '4px',
//								color : 'rgba(0, 0, 0, .2)',
//								zeroLineColor : 'transparent'
//							},
//							ticks : $.extend({
//								beginAtZero : true,
//								suggestedMax : 200
//							}, ticksStyle)
//						} ],
//						xAxes : [ {
//							display : true,
//							gridLines : {
//								display : false
//							},
//							ticks : ticksStyle
//						} ]
//					}
//				}
//			})
//	
//	
	
	
	
	
	
	
	
	
	
	
	
	
	//Latency & Packet Drop
	
	/* Real time Bandwidth Utilization */

	var l = window.location;
	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
	//var serviceUrl = base_url + "/dashboard/interfaceBandwidthUtil";
	var serviceUrl = base_url + "/nodeDashboard/nodeLatencyStatusDetails";
	$.ajax({
		type : 'POST',
		url : serviceUrl,
		data : {
			ip_address : ip
		
		},
		dataType : 'json',
		success : function(jsondata) {
			//alert("Latnecy Data:"+jsondata);
			
			//alert(jsondata[0].latency)
			//alert(jsondata[0].packetLoss)
			
			Highcharts.stockChart('container_latency', {
			    chart: {
			        events: {
			            load: function () {
			            	
			                // set up the updating of the chart each second
			                var series = this.series[0];
			                var series2 = this.series[1];
			                
			                
			                setInterval(function () {
			          
			                	var l = window.location;
			                	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
			                	var serviceUrl = base_url + "/nodeDashboard/nodeLatencyStatusDetails";
			                	$.ajax({
			                		type : 'POST',
			                		url : serviceUrl,
			                		data : {
			                			ip_address : ip
			                			
			                		},
			                		dataType : 'json',
			                		success : function(jsondata1) {
			                	
			                			var x = (new Date()).getTime(), // current
																		// time
				                    		y=jsondata1[0].latency,
				                    		z=jsondata1[0].packetLoss;
				                    
				                    series.addPoint([x, y], true, true);
				                    series2.addPoint([x, z], true, true);
			                			
			                		}
			                		
			                	});
			              
			                }, 2000);
			               
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
			        name: 'Latency (ms)',
			        data: (function () {
			            // generate an array of random data
			            var data = [],
			                time = (new Date()).getTime(),
			                i;

			            for (i = -999; i <= 0; i += 1) {
			                data.push([
			                    time + i * 1000,
			                    jsondata[0].latency
			                ]);
			            }
			            return data;
			        }())
			    },
			    {
			        name: 'Packet Loss(%)',
			        data: (function () {
			            // generate an array of random data
			            var data = [],
			                time = (new Date()).getTime(),
			                i;

			            for (i = -999; i <= 0; i += 1) {
			                data.push([
			                    time + i * 1000,
			                    jsondata[0].packetLoss
			                ]);
			            }
			            return data;
			        }())
			    }]
			});
			

		}
	});
	
	
	//CPU and Memory Utilization
	var l = window.location;
	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
	//var serviceUrl = base_url + "/dashboard/interfaceBandwidthUtil";
	var serviceUrl = base_url + "/nodeDashboard/cpuAndMemoryUtilization";
	$.ajax({
		type : 'POST',
		url : serviceUrl,
		data : {
			ip_address : ip
		
		},
		dataType : 'json',
		success : function(jsondata) {
			//alert("Latnecy Data:"+jsondata);
			
			//alert(jsondata[0].latency)
			//alert(jsondata[0].packetLoss)
			
			Highcharts.stockChart('interactive', {
			    chart: {
			    	//type : 'area',
			        events: {
			            load: function () {
			            	
			                // set up the updating of the chart each second
			                var series = this.series[0];
			                var series2 = this.series[1];
			                
			                
			                setInterval(function () {
			          
			                	var l = window.location;
			                	var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
			                	var serviceUrl = base_url + "/nodeDashboard/cpuAndMemoryUtilization";
			                	$.ajax({
			                		type : 'POST',
			                		url : serviceUrl,
			                		data : {
			                			ip_address : ip
			                			
			                		},
			                		dataType : 'json',
			                		success : function(jsondata1) {
			                	
			                			var x = (new Date()).getTime(), // current
																		// time
				                    		y=jsondata1[0].cpuUtilization,
				                    		z=jsondata1[0].memoryUtilization;
				                    
				                    series.addPoint([x, y], true, true);
				                    series2.addPoint([x, z], true, true);
			                			
			                		}
			                		
			                	});
			              
			                }, 2000);
			               
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
			                    jsondata[0].cpuUtilization
			                ]);
			            }
			            return data;
			        }())
			    },
//			    {
//			        name: 'Memory Utilization',
//			        data: (function () {
//			            // generate an array of random data
//			            var data = [],
//			                time = (new Date()).getTime(),
//			                i;
//
//			            for (i = -999; i <= 0; i += 1) {
//			                data.push([
//			                    time + i * 1000,
//			                    jsondata[0].memoryUtilization
//			                ]);
//			            }
//			            return data;
//			        }())
//			    }
			    ]
			});
			

		}
	});
	
	
})

// lgtm [js/unused-local-variable]
