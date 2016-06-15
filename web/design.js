/**
 * Created by aravind on 2/21/2016.
 */

$(function(){
    d3.csv("data.csv", function(data){
        d3.csv("static.csv", function(results){
            processData(data, results);
        });
    });

    $('#searchQuery').keyup(function(e) {
        if (e.keyCode == 13) {
            $('#searchSubmit').trigger('click');
        }
    });

    function processData(data,results){
        var queries = d3.select('#queries')
            .selectAll('div')
            .data(data)
            .enter() .append('a')
            .attr('href',function(d,i){
                return '#'+'div'+i;
            })
            .style('text-decoration','none')
            .append('div')
            .attr('id', function(d,i){
                return 'div'+i;
            })
            .attr('class','list-group-item text2')
            .html(function(d,i) { return d.text; })
            .on("mouseover", function(d,i){
                var current = d3.select(this);

                if(current.select('#queryResults'+i)[0][0] == null)
                    current.style("background-color","lightblue");


                d3.select(this).select('#tip'+i)
                    .transition()
                    .duration(10)
                    .style("opacity", 1);

                d3.select(this).select('#tip'+i)
                    .style("visibility","visible");
            })
            .on("mouseout", function(d,i){

                d3.select(this).style("background-color","white");
                d3.select(this).select('#tip'+i).style("visibility","hidden");

            })
            .on("click", function(d,i) {
                for(j=0;j<10;j++){
                    if (j!=i)
                        $('#queryResults'+j).remove();
                }
                var current = d3.select(this);
                if(current.select('#queryResults'+i)[0][0] === null){
                    current.append('div')
                    .attr('id', function(){
                        return 'queryResults'+i;
                    })
                    .attr('class','text');
                        $.when(
                         createHtml(results[i].result,i)).then(function (){
                            setHtml(i);
                        });
                }
            })
            .on("dblclick", function(d,i) {
                $('#queryResults'+i).remove();
            })
            .append('div')
            .attr('id', function (d,i) {
                return 'tip'+i;
            })
            .attr('class','tooltip')
            .style('visibility','hidden')
            .style("right", (-140) + "px")
            .style("top", (0) + "px")
            .html(function(){return "Click to see recommendations. Double Click to hide recommendations."});
            //.on("click", function(d,i){
            //    d3.select(this).style("background-color","white");
            //    var current = d3.select(this)
            //        .select("#queryResults"+i)
            //        .style('visibility','visible');
            //})
            //.append('div')
            //.attr('id', function(d,i){
            //    return 'queryResults'+i;
            //})
            //.style('visibility','hidden')
            //.html(function(d,i){
            //     createHtml(results[i].result,i);
            //     return $('#results').html();
            //});
    }
    function setHtml(i){
        $('#queryResults'+i).html($('#results').html());
    }

    $('#results a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });

    $("#results").toggle(false);
    $("#toggle").click(function() {
        if ($("#toggle").text() === 'Hide the Queries') {
            $('#queries').toggle(false);
            $("#toggle").text('Show the Queries');
            $("#results").toggle(false);
        }
        else{
            $('#queries').toggle(true);
            $("#toggle").text('Hide the Queries');
            $("#results").toggle(false);
        }
    });

    $("#searchSubmit").click(function(){
        $("#results").toggle(false);
        var query = $("#searchQuery").val();
        if(query==='')
            alert("Enter a search query");
        else{
            $.ajax({
                url: 'http://localhost:8080/search', //?query='+query,
                data: {
                    query: query
                },
                dataType: 'json',
                cache:false,
                success : function(data){
                    data = JSON.stringify(data)
                    createHtml(data,20);
                    $("#results").toggle(true);
                }
            });
        }

    });

    function createHtml(data,index){
        var temp= JSON.parse(data);
        //$("#results").html( b['result']);
        var topHits = temp['result'];
        for(i=0;i<topHits.length;i++){
            $('#results li:eq('+i+') a').attr('href','#'+ i.toString()+index.toString());
            $('.content div:eq('+i+')').attr('id', i.toString()+index.toString());
            $('.content div:eq('+i+')').html(topHits[i]);
        }
    }
});

