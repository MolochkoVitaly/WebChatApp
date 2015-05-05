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
    token: 'TE11EN'
};

function storeMessages(sendMessage, continueWith) {
    post(appState.mainUrl, JSON.stringify(sendMessage),
        function () {
            //updateMessages();
            restoreMessages();
    });
}

function restoreMessages(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;

    get(url, function (responseText) {
        console.assert(responseText != null);
        delegateEventServer();
        var response = JSON.parse(responseText);

        appState.token = response.token;
        createAllTasks(response.messages);

        continueWith && continueWith();
    });
}

function updateMessages(continueWith) {
    var url = appState.mainUrl + '?token=' + appState.token;

    get(url, function (responseText) {
        console.assert(responseText != null);
        delegateEventServer();
        var response = JSON.parse(responseText).messages;
        for (var i = 0; i < response.length; i++) {
            var message = response[i];
            if (message.request == "POST") {
                addAllMessages(message);
            }
            if (message.request == "PUT") {
                addChangeMessage(message);
            }
            if (message.request == "DELETE") {
                addDeleteMessage(message);
            }
        }
        continueWith && continueWith();
    });
    //setTimeout(updateMessages, 1000);

}

function createAllTasks(allTasks) {
    for(var i = 0; i < allTasks.length; i++){
        addAllMessages(allTasks[i]);
    }
}

function addAllMessages(message) {
    if (appState.messageList[message.id] == null) {
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
        $('.exampleMessage')[message.id + 1].getElementsByClassName('message')[0].innerHTML=message.message;
        appState.messageList[message.id] = message;
    }
}

function addDeleteMessage(message) {
    if (appState.messageList[message.id] != null) {
        $('.exampleMessage')[message.id + 1].getElementsByClassName('message')[0].innerHTML=message.message;
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

function store(listToSave) {

    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    localStorage.setItem("Chat messageList", JSON.stringify(listToSave));
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
        restoreMessages();
    });
}
function deleteMessage(index,continueWith) {
 var indexToken = index*8+11; 
 var url = appState.mainUrl + '?token=' + "TN" +indexToken.toString() + "EN";
    del(url, function () {

     continueWith && continueWith();
    });
}

$(document).ready(function () {

    $userName = $('h4.currentName');
    $inputChange = $('#changeName');

    restoreMessages();
    //updateMessages();
    $userName.html(restoreName() || "Имя пользователя");

    if($userName.html()!=""){
        li = $('#onlineArea li').first().clone();
        li.html($userName.html());
        $('#onlineArea').append(li.show());

    }

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
            return;
        }

        id = $(this).closest('.exampleMessage').attr('message-id');
        
        deleteMessage(id,
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
                appState.messageList.push(task);
                messageDiv = $('.exampleMessage').first().clone();
                messageDiv.find('.nick').html($userName.html() + ":");
                messageDiv.find('.message').html(message);
                messageDiv.attr('message-id', uniqueId());
                $('#showMessage').append(messageDiv.show());
                $('#messageArea').val('');
            });
            $('#messageArea').val('');
        };
    })

    //edit message (only my msg)
    $('#showMessage').on('click', 'a.editMessage', function () {
        if($userName.html() + ":" != $(this).closest('.exampleMessage').find('.nick').html()){
            return;
        }
        if( $(this).closest('.exampleMessage').find('.message').html() == "message has deleted.") {
            alert("This message don't editable, so it was deleted");
            return;
        };

        $p = $(this).closest('.exampleMessage')
        $message = $p.find('.message')
        $input = $p.find('#changeMessage')
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
        $p = $(this).closest('.exampleMessage')
        $input = $p.find('#changeMessage')
        editer = $input.val();
        $input.attr('disabled', true);
        $input.hide();
        id = $(this).closest('.exampleMessage').attr('message-id');
        task = theMessage(editer,$p.find('.nick').html(),id);
        changeMessages(task,function(){
            $p.find('.message').html(task.msgText).show();
        });
        //$p.find('.message').html(task.msgText).show();
        $p.find('.nick').show();
        $(this).hide();
        $p.find('a.editMessage').show();
        $p.find('.close').show();

        for(var i = 0; i < appState.messageList.length; i++) {
            if(appState.messageList[i].id != id)
                continue;

            appState.messageList[i].msgText = editer;
            store(appState.messageList);

            return;
            }
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
function del(url, continueWith, continueWithError) {
    ajax('DELETE', url, null, continueWith, continueWithError);
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

        if (xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if (isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
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
