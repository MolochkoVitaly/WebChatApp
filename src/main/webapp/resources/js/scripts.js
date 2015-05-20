var uniqueId = function() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random).toString();
};

var theMessage = function(text, user , value) {
    return {
        msgText: text,
        userName: user,
        id: value
    };
};

var appState = {
    mainUrl: 'chat',
    messageList:[],
    token: 'TE0EN'
};

function storeMessages(sendMessage, continueWith) {
    post(appState.mainUrl, JSON.stringify(sendMessage),
        function () {
        });
}


function restoreMessages(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;

    get(url, function (responseText) {
        console.assert(responseText != null);
        delegateEventServer();
        var response = JSON.parse(responseText);

        createAllTasks(response.messages);

        continueWith && continueWith();
    });
    document.getElementById("showMessage").scrollTop = document.getElementById("showMessage").scrollHeight;
}

function updateMessages(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;

    get(url, function (responseText) {
        console.assert(responseText != null);
        delegateEventServer();
        appState.token = JSON.parse(responseText).token;
        var response = JSON.parse(responseText).messages;
        for (var i = 0; i < response.length; i++) {
            var message = response[i];
            if(message.changeDate == "not changed")
            {
                addAllMessages(message);
            }
            if (message.changeDate != "not changed" && message.isDeleted == 'false') {
                addChangeMessage(message);
            }
            if (message.isDeleted == 'true') {
                addDeleteMessage(message);
            }
        }
        continueWith && continueWith();
    });
    setTimeout(updateMessages, 1000);
    document.getElementById("showMessage").scrollTop = document.getElementById("showMessage").scrollHeight;
}

function createAllTasks(allTasks) {
    for(var i = 0; i < allTasks.length; i++){
        addAllMessages(allTasks[i]);
    }
    document.getElementById("showMessage").scrollTop = document.getElementById("showMessage").scrollHeight;
}

function addAllMessages(message) {
    if (appState.messageList[parseInt(message.id, 10)] == null) {
        task = message;
        messageDiv = $('.exampleMessage').first().clone();
        messageDiv.find('.nick').html(task.userName + ":");
        messageDiv.find('.message').html(task.msgText);
        messageDiv.attr('message-id', task.id);
        $('#showMessage').append(messageDiv.show());
        appState.messageList.push(message);
    }
}

function addChangeMessage(message) {
    if (appState.messageList[message.id] != null) {
        $('.exampleMessage').find('.message')[parseInt(message.id,10) + 1].innerText=message.msgText;
        appState.messageList[message.id] = message;
    }
}

function addDeleteMessage(message) {
    if (appState.messageList[message.id] != null) {
        $('.exampleMessage').find('.message')[parseInt(message.id, 10) + 1].innerText=message.msgText;
        appState.messageList[message.id] = message;
    }
}

function delegateEventServer(evtObj) {
    $("#server").removeClass('btn btn-danger');
    $("#server").addClass('btn btn-success');
}

function defaultErrorHandler(message) {
    $("#server").removeClass('btn btn-success');
    $("#server").addClass('btn btn-danger');
}


function restoreName(){
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    var item = localStorage.getItem("Chat userName");


    $('#messageArea').attr('disabled', false);
    $('#send').attr('disabled', false);

    return item && JSON.parse(item);
}

function changeMessages(changeMessage, continueWith) {
    put(appState.mainUrl, JSON.stringify(changeMessage), function () {
    });
}

function deleteMessage(index, msg, continueWith) {
    var indexToken = index;
    var url = appState.mainUrl + '?token=' + "TN" +indexToken.toString() + "EN";
    del(url, JSON.stringify(msg), function () {

        continueWith && continueWith();
    });
}

$(document).ready(function () {

    $userName = $('h4.currentName');
    $inputChange = $('#changeName');

    restoreMessages();
    document.getElementById("showMessage").scrollTop = document.getElementById("showMessage").scrollHeight;
    updateMessages();
    $userName.html(restoreName() || "Имя пользователя");

    //enter in chat
    $('#submitUser').click(function () {
        if ($('#userLogin').val() != "") {
            userName = $('#userLogin').val();
            $userName.html(userName);
            $('#userLogin').val('');
            $('#messageArea').attr('disabled', false);
            $('#send').attr('disabled', false);
            li = $('#onlineArea li').first().clone();
            li.html($userName.html());
            $('#onlineArea').append(li.show());

            localStorage.setItem("Chat userName", JSON.stringify($userName.html()));
        };
    })

    //change name
    $('#changeCurrentName').click(function () {
        name = $userName.html();
        $userName.hide();
        $inputChange.attr('disabled', false);
        $inputChange.val(name);
        $inputChange.show();
        $(this).hide();
        $('#saveCurrentName').show();
    })

    // save change name
    $('#saveCurrentName').click(function () {
        name = $inputChange.val();
        if (name != "") {
            $inputChange.attr('disabled', true);
            $inputChange.hide();
            $userName.html(name);
            $(this).hide();
            $('#changeCurrentName').show()
            $userName.show();

            localStorage.setItem("Chat userName", JSON.stringify($userName.html()));
        };
    })

    //delete message (only my msg)
    $('#showMessage').on('click', 'button.close', function () {
        if($userName.html() + ":" != $(this).closest('.exampleMessage').find('.nick').html()){
            alert("You don't delete this message!");
            return;
        }

        if($(this).closest('.exampleMessage').find('.message').html() == "isDeleted") {
            alert("This message don't delete, so it was deleted");
            return;
        };

        id = $(this).closest('.exampleMessage').attr('message-id');
        text= $(this).closest('.exampleMessage').find('.message').html();
        name = $(this).closest('.exampleMessage').find('.nick').html();
        task = theMessage(text, name, id)
        deleteMessage(id, task,
            function(){
            });
    })

    //send message
    $('#send').click(function () {

        message = $('#messageArea').val();
        if (message != "") {
            task = theMessage(message,$userName.html(),appState.messageList.length);
            storeMessages(task,
                function () {
                });
            $('#messageArea').val('');
        };
        document.getElementById("showMessage").scrollTop = document.getElementById("showMessage").scrollHeight;
    })

    //edit message (only my msg)
    $('#showMessage').on('click', 'a.editMessage', function () {
        if($userName.html() + ":" != $(this).closest('.exampleMessage').find('.nick').html()){
            return;
        }
        if( $(this).closest('.exampleMessage').find('.message').html() == "isDeleted") {
            alert("This message don't editable, so it was deleted");
            return;
        };

        $p = $(this).closest('.exampleMessage');
        $message = $p.find('.message');
        $input = $p.find('#changeMessage');
        editer = $message.html();
        $message.hide();
        $p.find('.nick').hide();
        $input.attr('disabled', false);
        $input.val(editer);
        $input.show();
        $(this).hide();
        $p.find('.close').hide();
        $p.find('#saveMessage').show();
    })

    //save edit message
    $('#showMessage').on('click', 'button#saveMessage', function () {
        $p = $(this).closest('.exampleMessage');
        $input = $p.find('#changeMessage');
        editer = $input.val();
        $input.attr('disabled', true);
        $input.hide();
        id = $(this).closest('.exampleMessage').attr('message-id');
        task = theMessage(editer,$p.find('.nick').html(),id);
        changeMessages(task, function(){
        });
        $p.find('.message').show();
        $p.find('.nick').show();
        $(this).hide();
        $p.find('a.editMessage').show();
        $p.find('.close').show();

        for(var i = 0; i < appState.messageList.length; i++) {
            if(appState.messageList[i].id != id)
                continue;

            appState.messageList[i].msgText = editer;

            return;
        }
    })

    $('.smile').click(function () {
        //alert($(this).html());
        var smile = $(this).html();
        var input =  $('#messageArea').val();
        $('#messageArea').val(input + " " + smile);
    })
})

function get(url, continueWith, continueWithError) {
    ajax('GET', url, null, continueWith, continueWithError);
}
function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
}
function put(url, data, continueWith, continueWithError) {
    ajax('PUT', url, data, continueWith, continueWithError);
}
function del(url, data, continueWith, continueWithError) {
    ajax('DELETE', url, data, continueWith, continueWithError);
}
function isError(text) {
    if (text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch (ex) {
        return true;
    }

    return !!obj.error;
}
function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {

        if (xhr.readyState != 4)
            return;

        if(xhr.status != 304) {

            if (xhr.status != 200) {
                continueWithError('Error on the server side, response ' + xhr.status);
                return;
            }

            if (isError(xhr.responseText)) {
                continueWithError('Error on the server side, response ' + xhr.responseText);
                return;
            }
        }

        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    }

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n' +
            '\n' +
            'Check if \n' +
            '- server is active\n' +
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}