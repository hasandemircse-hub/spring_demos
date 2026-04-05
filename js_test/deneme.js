// -------------------------------------------------------
// BÖLÜM 1: DEĞİŞKENLER
// -------------------------------------------------------

// let   → değeri sonradan değişebilir
// const → bir kez atanır, değiştirilemez
// var   → eski yöntem, artık kullanılmıyor

let isim = "Hasan";
isim = "Ali";           // geçerli — let değiştirilebilir

const yas = 25; // const değiştirilemez — yas = 30 yazmak HATA verir

console.log(isim, yas); // Ali 25

// -------------------------------------------------------
// BÖLÜM 2: VERİ TİPLERİ
// -------------------------------------------------------

const metin   = "Merhaba";  // string  — yazı
const sayi    = 42;         // number  — tam sayı
const ondalik = 3.14;       // number  — ondalik da aynı tip
const dogru   = true;       // boolean — true veya false
const yanlis  = false;
const bos     = null;       // null      — bilerek boş bırakıldı
const tanimsiz = undefined; // undefined — değer hiç atanmamış

console.log(typeof metin);  // "string"
console.log(typeof sayi);   // "number"
console.log(typeof dogru);  // "boolean"

// -------------------------------------------------------
// BÖLÜM 3: KOŞULLAR — if / else
// -------------------------------------------------------

const puan = 75;

if (puan >= 90) {
    console.log("Pekiyi");
} else if (puan >= 70) {
    console.log("İyi");     // bu çalışır çünkü 75 >= 70
} else if (puan >= 50) {
    console.log("Orta");
} else {
    console.log("Başarısız");
}

// Kısa yol: tek satır koşul (ternary operator)
// koşul ? doğruysa : yanlışsa
const sonuc = puan >= 50 ? "Geçti" : "Kaldı";
console.log(sonuc); // "Geçti"

// -------------------------------------------------------
// BÖLÜM 4: DİZİLER (Array)
// -------------------------------------------------------

// Köşeli parantez ile tanımlanır, index sıfırdan başlar
const meyveler = ["elma", "armut", "muz"];

console.log(meyveler[0]);      // "elma"  — ilk eleman
console.log(meyveler[2]);      // "muz"   — üçüncü eleman
console.log(meyveler.length);  // 3       — kaç eleman var

meyveler.push("çilek");        // sona ekle → ["elma", "armut", "muz", "çilek"]
meyveler.pop();                // sondan sil → ["elma", "armut", "muz"]
console.log(meyveler);

// -------------------------------------------------------
// BÖLÜM 5: DÖNGÜLER
// -------------------------------------------------------

// for döngüsü: kaç kez döneceğini biliyorsun
for (let i = 0; i < meyveler.length; i++) {
    console.log(i, meyveler[i]); // 0 elma, 1 armut, 2 muz
}

// forEach: dizi üzerinde daha sade döngü
meyveler.forEach(meyve => {
    console.log("Meyve:", meyve);
});

// -------------------------------------------------------
// BÖLÜM 6: FONKSİYONLAR
// -------------------------------------------------------

// Klasik tanım
function topla(a, b) {
    return a + b; // return → fonksiyonun sonucunu dışarı çıkar
}

// Arrow function — daha kısa yazım, aynı iş
const carp = (a, b) => a * b;

console.log(topla(3, 5)); // 8
console.log(carp(3, 5));  // 15

// Parametreye varsayılan değer verebilirsin
function selamla(ad = "Misafir") {
    console.log("Merhaba, " + ad + "!");
}

selamla("Hasan"); // "Merhaba, Hasan!"
selamla();        // "Merhaba, Misafir!" — parametre verilmedi, varsayılan kullanıldı

// -------------------------------------------------------
// BÖLÜM 7: OBJELER
// -------------------------------------------------------

// Obje: birbiriyle ilgili verileri bir arada tutar
const kullanici = {
    ad: "Hasan",
    yas: 25,
    aktif: true,
    selamVer: function () {     // obje içinde fonksiyon — "method" denir
        console.log("Merhaba, ben " + kullanici.ad);
    }
};

console.log(kullanici.ad);      // "Hasan"  — nokta ile erişim
console.log(kullanici["yas"]);  // 25       — köşeli parantez ile de erişilebilir
kullanici.selamVer();           // "Merhaba, ben Hasan"

kullanici.email = "hasan@mail.com"; // sonradan yeni alan eklenebilir
console.log(kullanici.email);       // "hasan@mail.com"

// -------------------------------------------------------
// BÖLÜM 8: TEMPLATE LITERAL — String içine değişken göm
// -------------------------------------------------------

const kullaniciAd = "Hasan";
const kullaniciYas = 25;

// Eski yöntem (+ ile birleştirme — karmaşık uzun yazımlarda okunması zor)
console.log("Adım " + kullaniciAd + ", yaşım " + kullaniciYas);

// Yeni yöntem: backtick (`) ve ${} kullan
console.log(`Adım ${kullaniciAd}, yaşım ${kullaniciYas}`); // çok daha okunabilir

// ${} içine işlem de yazılabilir
console.log(`5 yıl sonra yaşım: ${kullaniciYas + 5}`); // "5 yıl sonra yaşım: 30"
