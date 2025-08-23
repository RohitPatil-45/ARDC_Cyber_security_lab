/*
 Template Name: Upcube - Bootstrap 4 Admin Dashboard
 Author: Themesdesign
 Website: www.themesdesign.in
 File: C3 Chart init js
 */


//RAMGraphAction
!function(e){
    "use strict";
    var a=function(){   
    };
    a.prototype.init=function(){
        var aa;
        var bb;
        var cc;
        var dd;
        var xData = [];
        var xData1 = [];
        $.ajax({
            url:'RAMGraphAction',
            type:'post',
            data:'ip_address='+$('#ip_add').val(),
            success: function(myBooks) {              
                var d=myBooks.split(",");            
                aa=d[0];
                bb=d[1];
                  cc=d[2];
                dd=d[3];
                document.getElementById('free').innerHTML= d[0]+"%";
                 document.getElementById('used').innerHTML= d[1]+"%";
                  document.getElementById('free1').innerHTML= d[2];
                 document.getElementById('used1').innerHTML= d[3];
                xData.push(aa);
                xData1.push(bb);
                c3.generate({

                    bindto:"#pie-chart",
                    data:{
                        columns:[["Free RAM",xData],["Used RAM",xData1]],
                        type:"pie"
                    },
                    color:{
                        pattern:["#0097a7","#f32f53","#f32f53","#0097a7"]
                    },
                    pie:{
                        label:{
                            show:!1
                        }
                    }
                })          
            }
        });      
    },e.ChartC3=new a,e.ChartC3.Constructor=a
}(window.jQuery),function(e){
    "use strict";
    e.ChartC3.init()
}(window.jQuery);



