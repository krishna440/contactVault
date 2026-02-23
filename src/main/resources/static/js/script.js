console.log("script Loaded")

// change theme work
let currentTheme = getTheme();
// initially --->

document.addEventListener('DOMContentLoaded',() =>{
    changeTheme();
});

// TODO
function changeTheme(){
    // Set  to web page
    changePageTheme(currentTheme, currentTheme);

    // Set the listener to change theme button
    const changeThemeButton = document.querySelector("#theme_change_button");

        const oldTheme = currentTheme;
        changeThemeButton.addEventListener("click",(event) => {
        const oldTheme = currentTheme;
        console.log("change theme button clicked");


        if(currentTheme === "dark"){
            // Theme ko light krna hai
            currentTheme = "light";
        }else{
            // theme ko dark krna hai
            currentTheme="dark";
        } 

        changePageTheme(currentTheme, oldTheme);
    });
}


// Set Theme to localStorage

function setTheme(theme){
    localStorage.setItem("theme", theme)
}


// get theme from localStorage

function getTheme() {
    let theme = localStorage.getItem("theme");
    return theme ? theme : "light";
}


// Change current page theme
function changePageTheme(theme, oldTheme){
    // Local Storga me update
        setTheme(currentTheme);

        // remove the current theme
         document.querySelector("html").classList.remove(oldTheme)


        // Set the current theme
        document.querySelector("html").classList.add(theme);

        // change the text of button
        document
        .querySelector('#theme_change_button')
        .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
}
// End of change theme work