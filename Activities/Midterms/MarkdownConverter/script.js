const textInput = document.getElementById("markdown-input");
const rawHtml = document.getElementById("html-output");
const htmlPreview = document.getElementById("preview");
const headingRegex = /^\#{1,3}\s/;
const boldRegex = /\*\*.+\*\*|__.+__/;
const italicRegex = /\*.+\*|_.+_/;
const blockRegex = /^>\s+/;
const imgRegex = /\!\[.+\]\(.+\)/;
const linkRegex = /\[.+\]\(.+\)/;
const bracketCheck = /\[.+\]/;
const parenthesisCheck = /\(.+\)/;

function convertMarkdown() {
  const text = textInput.value.split("\n");
  const textEdit = textInput.value.split("\n");

  for (let i = 0; i < text.length; i++) {
    let headingMatch = textEdit[i].match(headingRegex);
    if (headingMatch) {
      for (let j = 0; j < headingMatch.length; j++) {
        let heavy = headingMatch[j].length - 1;
        textEdit[i] = `<h${heavy}>${text[i].slice(heavy + 1)}</h${heavy}>`;
      }
    }

    let blockMatch = textEdit[i].match(blockRegex);
    if (blockMatch) {
      for (let j = 0; j < blockMatch.length; j++) {
        textEdit[i] = `<blockquote>${text[i].slice(2)}</blockquote>`;
      }
    }

    let boldMatch = textEdit[i].match(boldRegex);
    if (boldMatch) {
      for (let j = 0; j < boldMatch.length; j++) {
        textEdit[i] = textEdit[i].replace(boldRegex, `<strong>${boldMatch[j].slice(2, -2)}</strong>`);
      }
    }

    let italicMatch = textEdit[i].match(italicRegex);
    if (italicMatch) {
      for (let j = 0; j < italicMatch.length; j++) {
        textEdit[i] = textEdit[i].replace(italicRegex, `<em>${italicMatch[j].slice(1, -1)}</em>`);
      }
    }
    
    let imgMatch = textEdit[i].match(imgRegex);
    if (imgMatch) {
      for (let j = 0; j < imgMatch.length; j++) {
        let alt = imgMatch[j].match(bracketCheck)
        let imglink = imgMatch[j].match(parenthesisCheck)

        textEdit[i] = textEdit[i].replace(imgRegex, `<img alt = "${alt[j].slice(1, -1)}" src = "${imglink[j].slice(1, -1)}">`);
      }
    }

    let linkMatch = textEdit[i].match(linkRegex);
    if (linkMatch) {
      for (let j = 0; j < linkMatch.length; j++) {
        let linkText = linkMatch[j].match(bracketCheck)
        let link = linkMatch[j].match(parenthesisCheck)

        textEdit[i] = textEdit[i].replace(linkRegex, `<a href = "${link[j].slice(1, -1)}">${linkText[j].slice(1, -1)}</a>`);
      }
    }
  }

  return textEdit.join("\n");
}

textInput.addEventListener("input", function () {
  rawHtml.textContent = `${convertMarkdown()}`;
  htmlPreview.innerHTML = convertMarkdown();
});
