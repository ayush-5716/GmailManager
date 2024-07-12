// Initialization for ES Users


document.addEventListener('DOMContentLoaded', (event) => {
    const menuItem = `
    <div class="inbox">
        <div style="grid-area: up; justify-self: center; align-self: center;" class="p1">
        180
        </div>

        <div style="grid-area: down" class="button-area">
            <div style="grid-area: first; margin: 1px">
                <a href="https://google.com" class="button-24"> Open! </a>
            </div>
            <div style="grid-area: second; margin: 1px">
                <button type="button" style="background: #FF4742;" class="button-24">Block!</button>
            </div>
            <div style="grid-area: third; margin: 1px">
                <button type="button" style="background: #dbe455;" class="button-24">Delete!</button>
            </div>
        </div>

    </div>
    `
    const wrapper = document.querySelectorAll('.wrapper');
    wrapper.forEach(wrap => {
        wrap.innerHTML = `
        ${menuItem}
      `
    })
    wrapper.innerHTML = `
      ${menuItem}
    `
});