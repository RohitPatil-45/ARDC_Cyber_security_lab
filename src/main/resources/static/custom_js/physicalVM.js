window.onload = function() {

	setTimeout(
			function() {
				// alert($('#PhyIP_name').val());

				$.ajax({
					url : '/cloud_instance/nodeUpTimeStatus',
					data : {
						ip_address : $('#PhyIP_name').val()

					},
					success : function(data) {
						var data = JSON.parse(data);
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
								// labels: ['Feb 2022', 'Jan 2022', 'Dec 2021',
								// 'Nov 2021',
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

											// Include a dollar sign in the
											// ticks
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

				// Node Latency Live Graph
				$
						.ajax({
							url : '/cloud_instance/nodeLatencyStatus',
							data : {
								ip_address : $('#PhyIP_name').val()

							},
							success : function(jsondata) {
								var jsondata = JSON.parse(jsondata);
								Highcharts
										.stockChart(
												'vmLatency',
												{
													chart : {
														events : {
															load : function() {

																// set up the
																// updating of
																// the chart
																// each second
																var series = this.series[0];
																var series2 = this.series[1];

																setInterval(
																		function() {

																			var l = window.location;
																			var base_url = l.protocol
																					+ "//"
																					+ l.host
																					+ "/"
																					+ l.pathname
																							.split('/')[1];
																			var serviceUrl = base_url
																					+ "/cloud_instance/nodeLatencyStatus";
																			$
																					.ajax({
																						url : '/cloud_instance/nodeLatencyStatus',
																						data : {
																							ip_address : '172.16.5.24'

																						},
																						success : function(
																								jsondata1) {
																							var jsondata1 = JSON
																									.parse(jsondata1);
																							var x = (new Date())
																									.getTime(), // current
																							// time
																							y = jsondata1[0].latency, z = jsondata1[0].packetLoss;

																							series
																									.addPoint(
																											[
																													x,
																													y ],
																											true,
																											true);
																							series2
																									.addPoint(
																											[
																													x,
																													z ],
																											true,
																											true);

																						}

																					});

																		}, 2000);

															}
														}
													},

													accessibility : {
														enabled : false
													},

													time : {
														useUTC : false
													},

													rangeSelector : {
														buttons : [ {
															count : 1,
															type : 'minute',
															text : '1M'
														}, {
															count : 5,
															type : 'minute',
															text : '5M'
														}, {
															type : 'all',
															text : 'All'
														} ],
														inputEnabled : false,
														selected : 0
													},

													exporting : {
														enabled : false
													},

													series : [
															{
																name : 'Latency (ms)',
																data : (function() {
																	// generate
																	// an array
																	// of
																	// random
																	// data
																	var data = [], time = (new Date())
																			.getTime(), i;

																	for (i = -999; i <= 0; i += 1) {
																		data
																				.push([
																						time
																								+ i
																								* 1000,
																						jsondata[0].latency ]);
																	}
																	return data;
																}())
															},
															{
																name : 'Packet Loss(%)',
																data : (function() {
																	// generate
																	// an array
																	// of
																	// random
																	// data
																	var data = [], time = (new Date())
																			.getTime(), i;

																	for (i = -999; i <= 0; i += 1) {
																		data
																				.push([
																						time
																								+ i
																								* 1000,
																						jsondata[0].packetLoss ]);
																	}
																	return data;
																}())
															} ]
												});

							}
						});
			}, 1000);

}