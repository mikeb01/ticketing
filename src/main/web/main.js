var version = -1;

var dispatchTable = {
    "ALLOCATION_REJECTED": allocationRejected,
    "ALLOCATION_APPROVED": allocationApproved,
    "SECTION_UPDATED"    : sectionUpdated,
    "CONCERT_CREATED"    : concertCreated
};

function allocationRejected(event) {
    
}


function allocationApproved(event) {
    
}


function sectionUpdated(event) {
    var seats = sectionRow(event.concertId, event.sectionId).find(".availableSeats");
    seats.html(event.seatsAvailable);
}

function createConcert(concertId, name, venue) {
    var concertText = 
        '<div id="' + concertId + '" class="concert">' + 
        '    <span class="name">' + name + '</span>' +
        '    <span class="venue">' + venue + '</span>' +
        '    <table>' +
        '        <thead class="seating">' +
        '            <tr>' +
        '                <td>Section</td>' +
        '                <td>Price</td>' +
        '                <td>Total Seats</td>' +
        '                <td>Seats Remaining</td>' +
        '            </tr>' +
        '        </thead>' +
        '        <tbody>' +
        '        </tbody>' +
        '    </table>' +
        '</div>';
    return concertText;
}

function createSection(concertId, sectionId, name, price, seats) {
    var sectionText =
        '<tr class="' + sectionId + '">' +
        '    <td class="name">' + name + '</td>' +
        '    <td class="seats">' + seats + '</td>' +
        '    <td class="availableSeats">?</td>' +
        '    <td><input class="seatsToOrder" type="text" value="2"/></td>' +
        '    <td class="price"> @ ' + price + '</td>' + 
        '    <td><input class="buy" type="button" value="Buy"/></td>' +
        '</td>';
        
    return sectionText;
}

function sectionRow(concertId, sectionId) {
    return $("#" + concertId).find('.' + sectionId)
}

function concertTBody(concertId) {
    return $("#" + concertId).find("tbody");        
}

function concertCreated(event) {
    var concertId = event.concertId;
    
    $("body").append(createConcert(concertId, event.name, event.venue));
    
    var tbody = concertTBody(concertId);
    $.each(event.sections, function(i, section) {
        
        var sectionId = section.sectionId;
        tbody.append(createSection(concertId, sectionId, section.name, section.price, section.seats));
        
        sectionRow(concertId, sectionId).find('.buy').click(function() {
            var seats = sectionRow(concertId, sectionId).find('.seatsToOrder').val();
            placeOrder(concertId, sectionId, parseInt(seats));
        });
    });
}

function placeOrder(concertId, sectionId, seats) {
    
    var request = {
        'concertId': concertId, 
        'sectionId': sectionId,
        'numSeats': seats,
        'accountId': 12, 
        'requestId': 76
    };
    
    $.ajax({
        type: 'POST',
        url: 'request',
        contentType: 'application/json',
        data: JSON.stringify(request),
    });
}

$(document).ready(function() {
    poll();
});

function dispatch(event) {
    var func = dispatchTable[event.type];
    if (func) {
        func(event);
    }
}

function handleEvents(data, status, jqXHR) {
    
    var events = eval(data);
    
    for (var i in events) {
        var event = events[i];
        
        dispatch(event);
        
        if (event["version"] !== null) {
            version = Math.max(version, event.version);
        }
    }
    
    setTimeout(poll, 200);
}

function poll() {
    $.ajax({
        url: "response?account=12&version=" + version,
        context: document.body,
        type: "POST",
        success: handleEvents
    });
}