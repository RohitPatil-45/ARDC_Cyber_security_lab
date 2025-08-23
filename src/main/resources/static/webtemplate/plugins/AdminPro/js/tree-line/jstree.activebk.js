
(function ($) {
 "use strict";
 
 
 
     var l = window.location;
    var base_url = l.protocol + "//" + l.host + "/" + l.pathname.split('/')[1];
  var  serviceUrl= base_url + "/normaltree";
   
    alert("Ajax started "+serviceUrl)
    
    
 $.ajax({
			 url: serviceUrl, 
			 type:'GET',
			 success: function(result){
				 alert("ajax finished success " + result)
	   
	    
	    
	  }});
 
 
 
 
			
	
 $(function () {
	  $("#autho_atm_tree").jstree({
	    "plugins" : [ "search" ]
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

