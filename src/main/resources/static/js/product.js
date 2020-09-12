function deleteRow(r)
{
    var i=r.parentNode.parentNode.rowIndex;
    document.getElementById('usersTable').deleteRow(i);
    // ajax
}


/*function send (userID) {
    var  completeTable = document.getElementById("usersTable");
    completeTable.deleteRow(userID);
// ajax
}*/