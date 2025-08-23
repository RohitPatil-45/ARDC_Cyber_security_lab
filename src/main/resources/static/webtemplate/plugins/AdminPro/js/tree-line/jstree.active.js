
(function ($) {
 "use strict";
 
 
 
     var l = window.location;
    var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
  var  serviceUrl= base_url + "/normaltree";
   
  //  alert("Ajax started "+serviceUrl)
    
    
 $.ajax({
			 url: serviceUrl, 
			 type:'GET',
			 success: function(result){
				// alert("ajax finished success " + result)
	   
	    
	    
	  }});
 
 
 
 
			
	
 $(function () {
	    var mouse_x = 0;
	    var mouse_y = 0;
	    $(document).mousemove(function (event) {
	        mouse_x = event.pageX;
	        mouse_y = event.pageY;
	    });
	  $("#autho_atm_tree").jstree({
		
	  });
	  var to = false;
	  $('#searchip').keyup(function () {
		//  alert("hello")
	    if(to) { clearTimeout(to); }
	    to = setTimeout(function () {
	      var v = $('#searchip').val();
	      $('#autho_atm_tree').jstree(true).search(v);
	    }, 250);
	  });
	});
 
 
 $('#normal_atm_tree').jstree({
	  plugins: ["contextmenu"], 
	  contextmenu: {items: customMenu},
     'core' : {
     'data' : [
         {
             'text': 'NCR',
             'icon': 'fa fa-industry',  
             },
             {
                 'text': 'VORTEX',
                 'icon': 'fa fa-industry',  
                 },
                 {
                     'text': 'DIEBOLD',
                     'icon': 'fa fa-industry',  
                     },
                     {
                         'text': 'PERTO',
                         'icon': 'fa fa-industry',  
                         },
                         {
                             'text': 'VELOX',
                             'icon': 'fa fa-industry',  
                             },
                             {
                                 'text': 'DASH',
                                 'icon': 'fa fa-industry',  
                                 },
                    
            
         {
             'text': 'HITACHI',
             'icon': 'fa fa-industry',
             'state': {
                 'opened': true
             },
             'children': [
                 {
                     'text': '192.168.0.1',
                     'icon': 'fa fa-fax redmachine',                          
                     'state': {
                         'opened': true
                     }
                 },
                 {
                     'text': '192.168.1.189',    
                     'icon': 'fa fa-fax greenmachine',
                     'state': {
                         'opened': true
                     }
                 },
                 {
                     'text': '192.168.0.52',
                     'icon': 'fa fa-fax greenmachine',
                     'state': {
                         'opened': true
                     }
                 }
             ]
         },
        
     ],
    
 } 
 
 
 
 });






        $('#autho_atm_tree').jstree({
        	
//            var mouse_x = 0;
//            var mouse_y = 0;
//            $(document).mousemove(function (event) {
//                mouse_x = event.pageX;
//                mouse_y = event.pageY;
//            });
        	
        	
        	
        	plugins: ["contextmenu"], 
        	contextmenu: {items: customMenu},
            'core' : {
            'data' : [
                {
                    'text': 'NCR',
                    'icon': 'fa fa-industry',  
                    },
                    {
                        'text': 'VORTEX',
                        'icon': 'fa fa-industry',  
                        },
                        {
                            'text': 'DIEBOLD',
                            'icon': 'fa fa-industry',  
                            },
                            {
                                'text': 'PERTO',
                                'icon': 'fa fa-industry',  
                                },
                                {
                                    'text': 'VELOX',
                                    'icon': 'fa fa-industry',  
                                    },
                                    {
                                        'text': 'DASH',
                                        'icon': 'fa fa-industry',  
                                        },
                   
                {
                    'text': 'HITACHI',
                    'icon': 'fa fa-industry',
                    'state': {
                        'opened': true
                    },
                    'children': [
                        {
                            'text': '192.168.0.1',
                            'icon': 'fa fa-fax redmachine',                          
                            'state': {
                                'opened': true
                            }
                        },
                        {
                            'text': '192.168.1.189',    
                            'icon': 'fa fa-fax greenmachine',
                            'state': {
                                'opened': true
                            }
                        },
                        {
                            'text': '192.168.0.52',
                            'icon': 'fa fa-fax greenmachine',
                            'state': {
                                'opened': true
                            }
                        }
                    ]
                },
               
            ],
           
        } 
        
        
        
        });
	
	
})(jQuery); 

function customMenu(node) {
    // The default set of all items
	
	
	//alert(node)
    var items = {
        adminaccess: { 
            label: "Time Based Admin Access",
            icon:"fa fa-clock-o",
            
        },
        passwordreset: { 
            label: "Password Reset",
            icon:"glyphicon glyphicon-asterisk",
            
          
        },
        discoveruser: { 
            label: "Discover User",
            icon:"fa fa-user",
          
        },
        checkcredentials: { 
            label: "Check Credentials",
            icon:"fa fa-lock",
          
        },
        resetpolicy: { 
            label: "Reset Policy",
            icon:"fa fa-window-restore",
          
        },
        ejpull: {
            label: "EJ Pull",
            icon:"fa fa-files-o",
          
        },
        details: { // The "delete" menu item
            label: "Details",
            icon:"fa fa-info",
          
        }
    };

  

    return items;
}
