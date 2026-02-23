console.log("Contacts.js");
const baseURL = "http://localhost:8081"

const viewContactModal =
    document.getElementById('view_contact_modal');


// Flowbite modal instance
const options = {
    placement: 'center',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true
};

const instanceOptions = {
    id: 'view_contact_modal',
    override: true
};

const contactModal =
    new Modal(viewContactModal, options, instanceOptions);


// OPEN MODAL
function openContactModal() {

    const modal =
        document.getElementById("view_contact_modal");

    modal.classList.remove("hidden");

}


// CLOSE MODAL
function closeContactModal() {

    const modal =
        document.getElementById("view_contact_modal");

    modal.classList.add("hidden");

}
// LOAD CONTACT DATA INTO MODAL
async function loadContactdata(id) {

    const loader =
        document.getElementById("modal_loader");

    const content =
        document.getElementById("modal_content");

    const container =
        document.getElementById("modal_container");


    // show loader
    loader.classList.remove("hidden");
    content.classList.add("hidden");

    openContactModal();


    try {

        const response =
            await fetch(
                `${baseURL}/api/contacts/${id}`
            );

        if (!response.ok) {
            throw new Error("Contact not found");
        }

        const data =
            await response.json();


        console.log("Loaded contact:", data);


        // BASIC INFO
        document.getElementById("contact_name").innerText =
            data.name;

        document.getElementById("contact_email_top").innerText =
            data.email;

        document.getElementById("contact_phone").innerText =
            data.phoneNumber || "";


        // DETAILS
        document.getElementById("contact_address").innerText =
            data.address || "Not provided";

        document.getElementById("contact_description").innerText =
            data.description || "No description";


        // IMAGE
        document.getElementById("contact_image").src =
            data.picture || "/images/default.jpg";


        // LINKS
        document.getElementById("contact_website").innerText =
            data.webSiteLink || "Not available";

        document.getElementById("contact_website").href =
            data.webSiteLink || "#";


        document.getElementById("contact_linkedin").innerText =
            data.linkedInLink || "Not available";

        document.getElementById("contact_linkedin").href =
            data.linkedInLink || "#";


        // ACTION BUTTONS

        // CALL
        document.getElementById("contact_call").href =
            `tel:${data.phoneNumber}`;


        // EMAIL
        document.getElementById("contact_email_btn").href =
            `mailto:${data.email}`;


        // WHATSAPP
        document.getElementById("contact_whatsapp").href =
            `https://wa.me/${data.phoneNumber}`;


        // EDIT BUTTON
        document.getElementById("contact_edit_btn").onclick =
            function () {

                window.location.href =
                    `/user/contacts/view/${data.id}`;

            };


        // DELETE BUTTON  ✅ FIXED HERE
        document.getElementById("contact_delete_btn").onclick =
            function () {

                const confirmDelete =
                    confirm("Are you sure you want to delete this contact?");

                if (confirmDelete) {

                    window.location.href =
                        `/user/contacts/delete/${data.id}`;

                }

            };


        // hide loader
        loader.classList.add("hidden");
        content.classList.remove("hidden");


        // animation
        container.classList.remove("scale-95", "opacity-0");
        container.classList.add("scale-100", "opacity-100");

    }
    catch (error) {

        console.error("Error loading contact:", error);

        alert("Failed to load contact");

    }

}

async function deleteContact(id) {
    Swal.fire({
  title: "Do you want Delete the contact?",
  showDenyButton: true,
  confirmButtonText: "Delete",
}).then((result) => {
  if (result.isConfirmed) {
    const url = `${baseURL}/user/contacts/delete/`+ id;
    window.location.replace(url);
  }
});
}

function openShareModal(contactId) {

    const modal =
        document.getElementById("shareModal");

    const form =
        document.getElementById("shareForm");

    // SET FORM ACTION
    form.action =
        "/user/contacts/share/" + contactId;

    // SHOW MODAL
    modal.classList.remove("hidden");

    modal.classList.add("flex");

}

function closeShareModal() {

    const modal =
        document.getElementById("shareModal");

    modal.classList.add("hidden");

    modal.classList.remove("flex");

}

// OPEN REMINDER MODAL
function openReminderModal(contactId) {

    const modal =
        document.getElementById("reminderModal");

    document.getElementById("reminderContactId").value =
        contactId;

    modal.classList.remove("hidden");

    modal.classList.add("flex");

}


// CLOSE REMINDER MODAL
function closeReminderModal() {

    const modal =
        document.getElementById("reminderModal");

    modal.classList.add("hidden");

    modal.classList.remove("flex");

}


