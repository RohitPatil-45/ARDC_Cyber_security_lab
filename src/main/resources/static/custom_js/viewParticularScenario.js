$('.create-btn').click(function() {
    var instanceId = $(this).data('request-id');
    $('#cloudInstanceId').val(instanceId);
});