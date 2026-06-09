function renderAttendee(attendee){

    let statusHtml = "";

    if(attendee.checkedIn){

        statusHtml = `
            <p class="checked">
                ✓ Checked In at
                ${new Date(attendee.checkinTime).toLocaleTimeString(
                    "en-IN",
                    {
                        hour: "2-digit",
                        minute: "2-digit"
                    }
                )}
            </p>
        `;

    }else{

        statusHtml = `
            <p class="notchecked">
                ✗ Yet To Check In
            </p>
        `;
    }

    let imageUrl = attendee.photoUrl;

    if (imageUrl && imageUrl.includes("drive.google.com")) {

        const match = imageUrl.match(/\/d\/([^/]+)/);

        if (match) {

            imageUrl =
                `https://drive.google.com/uc?export=view&id=${match[1]}`;
        }
    }

console.log(attendee.photoUrl);
console.log(imageUrl);
    document.getElementById("attendeeCard").innerHTML = `

        <div class="card">

           <img
               src="${imageUrl}"
               class="photo"
               alt="Attendee Photo"
               onerror="console.log('IMAGE FAILED TO LOAD')"
           >

            <h2>${attendee.fullName}</h2>

            <p><b>Designation:</b> ${attendee.designation}</p>

            <p><b>Organization:</b> ${attendee.organizationName}</p>

            <p><b>Email:</b> ${attendee.emailId}</p>

            <p><b>Mobile:</b> ${attendee.mobileNumber}</p>

            ${statusHtml}

            ${
                !attendee.checkedIn
                ?
                `<button onclick="checkIn('${attendee.uid}')">
                    Check In
                </button>`
                :
                ""
            }

        </div>
    `;
}

function loadAttendee(uid){

    fetch(`/api/attendees/${uid}`)
        .then(response => response.json())
        .then(data => renderAttendee(data));
}

function checkIn(uid){

    fetch(`/api/attendees/${uid}/checkin`,{
        method:"PUT"
    })
    .then(response => response.json())
    .then(data => {

        alert("Check-In Successful");

        loadAttendee(uid);

    });
}

function searchAttendee(){

    let q = document.getElementById("search").value;

    if(q.length < 2){

        document.getElementById("searchResults").innerHTML = "";

        return;
    }

    fetch(`/api/attendees/search?q=${q}`)
        .then(response => response.json())
        .then(data => {

            let html = "";

            data.forEach(attendee => {

                html += `
                    <div
                        class="result"
                        onclick="loadAttendee('${attendee.uid}')"
                    >
                        <b>${attendee.fullName}</b><br>
                        ${attendee.organizationName}
                    </div>
                `;
            });

            document.getElementById("searchResults").innerHTML = html;
        });
}

function onScanSuccess(decodedText) {
    scanner.clear(); // stop scanner
    loadAttendee(decodedText);
}

const scanner = new Html5QrcodeScanner(
    "reader",
    {
        fps: 10,
        qrbox: 250
    }
);

scanner.render(
    onScanSuccess,
    error => {
        console.log(error);
    }
);

async function sendPendingPasses() {

    const btn =
        document.getElementById(
            "sendPassBtn"
        );

    btn.disabled = true;

    btn.innerText =
        "Sending...";

    try {

        const response =
            await fetch(
                "/api/attendees/send-pending-passes",
                {
                    method: "POST"
                }
            );

        const message =
            await response.text();

        alert(message);

    } catch(error) {

        alert(
            "Failed to send passes"
        );

        console.error(error);

    } finally {

        btn.disabled = false;

        btn.innerText =
            "Send Pending Passes";
    }
}

function loadDashboard() {

    fetch("/api/dashboard/stats")
        .then(response => response.json())
        .then(data => {

            document.getElementById(
                "totalRegistered"
            ).innerText =
                data.totalRegistered;

            document.getElementById(
                "checkedIn"
            ).innerText =
                data.checkedIn;

            document.getElementById(
                "pendingCheckIn"
            ).innerText =
                data.pendingCheckIn;

            document.getElementById(
                "passesSent"
            ).innerText =
                data.passesSent;

            document.getElementById(
                "passesPending"
            ).innerText =
                data.passesPending;
        });
}

loadDashboard();
