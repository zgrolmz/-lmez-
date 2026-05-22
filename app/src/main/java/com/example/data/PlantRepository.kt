package com.example.data

object PlantRepository {
    val plants = listOf(
        Plant(
            id = "ters_lale",
            name = "Ters Lale (Ağlayan Gelin)",
            scientificName = "Fritillaria imperialis",
            category = PlantCategory.ENDEMIC_PLANT,
            region = "Doğu Anadolu (Hakkari, Van, Adıyaman)",
            description = "Eşsiz salkım çiçekleri aşağı doğru baktığı ve her sabah çiçek yapraklarından damlalar aktığı için halk arasında 'Ağlayan Gelin' olarak da bilinir. Anadolu kültüründe hüznün ve asaletin simgesidir. İlkbaharda göz alıcı kırmızı ve turuncu renklerde çiçek açar.",
            benefits = "Estetik değerinin yanı sıra, yumruları geleneksel halk tıbbında küçük dozlarda göğsü yumuşatıcı olarak kullanılmıştır. Yoğun zehirli bileşikler içerebildiğinden bilinçsiz tüketilmemelidir.",
            soilRequirements = "Kum, mil karışımlı süzek (geçirgen), tınlı toprakları sever. Dipte killi, su tutan çamurlu toprakları sevmez, yumruları çürür.",
            wateringInstructions = "Yumrular ekildikten sonra can suyu verilir. İlkbaharda büyüme döneminde toprak nemli tutulmalı, çiçeklenme bittikten ve yazın uykuya geçtikten sonra neredeyse hiç sulanmamalıdır.",
            sunlight = "Yarı gölge alanlar ve sabah güneşini doğrudan alan, ferah rüzgarlı yamaçlar onun için idealdir.",
            sowingSeason = "Sonbahar (Eylül - Kasım ayları arasında toprak donmadan önce ekilmelidir).",
            conservationStatus = "VU",
            difficultyLevel = "Orta",
            wateringFrequency = 1,
            lightType = "Yarı Gölge"
        ),
        Plant(
            id = "zahter_kekigi",
            name = "Yayla Kekiği (Dağ Zahteri)",
            scientificName = "Thymbra spicata",
            category = PlantCategory.SPICE,
            region = "Akdeniz, Ege ve Güneydoğu Anadolu",
            description = "Zahter, yabani olarak kayalık yamaçlarda kendiliğinden yetişen şifalı ve yoğun kokulu bir kekik türüdür. Çiçekleri mor ve pembe tonlarındadır. Akdeniz mutfağının en temel lezzet ve şifa kaynaklarından biridir.",
            benefits = "Antiseptik ve hazmı kolaylaştırıcı özellikleri vardır. Çay olarak demlenir ya da salamura edilerek zeytinyağlı zahter salatası şeklinde tüketilir. Öksürüğe ve soğuk algınlığına çok iyi gelir.",
            soilRequirements = "Fakir, kireçli, çakıllı ve drenajı mükemmel, süzek toprakları tercih eder. Saksı yetiştiriciliğinde ponza taşı ile karıştırılmış bahçe toprağı kullanılmalıdır.",
            wateringInstructions = "Zahter kuraklığa çok dayanıklıdır. Toprağı tamamen kurumadan sulanmamalıdır. Fazla su kök çürümesine yol açacaktır. Haftada 1-2 kez hafif sulama yetebilir.",
            sunlight = "Tam gün bol güneş alan, güneye bakan, sıcak ve havadar yerleri sever.",
            sowingSeason = "İlkbahar (Mart - Nisan) tohumdan ekilebilir; sonbaharda ise çelikle üretim yapılabilir.",
            conservationStatus = "LC",
            difficultyLevel = "Kolay",
            wateringFrequency = 1,
            lightType = "Güneşli"
        ),
        Plant(
            id = "safran",
            name = "Safranbolu Safranı",
            scientificName = "Crocus sativus",
            category = PlantCategory.SPICE,
            region = "Batı Karadeniz (Safranbolu, Karabük)",
            description = "Dünyanın en pahalı baharatı olarak kabul edilen Safran, kendi ağırlığının 100 bin katı suyu altın sarısına boyayabilir. Sonbaharda mor renkli şık çiçekler açar. Bu çiçeklerin ortasındaki üç adet kırmızı tepecik (stigma) özenle elle toplanarak kurutulur.",
            benefits = "Güçlü bir antioksidandır, moral ve sakinlik vericidir. Hafızayı güçlendirir, hücreleri korur. Pilavlarda, tatlılarda (örneğin zerde) ve çaylarda eşsiz kokusuyla kullanılır.",
            soilRequirements = "Kum ve killi karışımlı, gevşek yapılı, bol kompostlu organik maddece zengin ancak kesinlikle iyi drene edilmiş topraklarda sağlıklı büyür.",
            wateringInstructions = "Fideler ilk çıktığında hafif sulanır. Doğa yağmurları genelde yeterli olur. Yaz kuraklığında yumrular uykuda iken hiç sulanmamalıdır, aksi takdirde çürür.",
            sunlight = "Günde en az 6-8 saat doğrudan güneş ışığı alan, rüzgardan korunaklı güney cepheleri sever.",
            sowingSeason = "Yaz sonu (Ağustos sonu - Eylül ortası yumru ekimi için en uygun dönemdir).",
            conservationStatus = "LC",
            difficultyLevel = "Zor",
            wateringFrequency = 1,
            lightType = "Güneşli"
        ),
        Plant(
            id = "dag_cayi",
            name = "Toros Dağ Çayı (Yayla Çayı)",
            scientificName = "Sideritis libanotica / tectorum",
            category = PlantCategory.ENDEMIC_PLANT,
            region = "Akdeniz Bölgesi (Toros Dağları - Antalya, Isparta)",
            description = "Torosların 1000-2000 metre yüksekliklerinde yetişen, üzeri yumuşak gri tüylerle kaplı endemik bir çalımsı bitkidir. Tüylü yapısı onu yüksek dağların kavurucu sıcağından ve kış soğuğundan korur. Kendine has hoş, limonumsu bir kokusu vardır.",
            benefits = "Türkiye'de kış aylarında 'yayla çayı' adıyla en çok tüketilen şifalı bitkilerdendir. Doğal bir bağışıklık güçlendiricidir, mideyi yatıştırır, sakinleştirici etkisi vardır.",
            soilRequirements = "Kuru, taşlı, kireçli, besin maddesi yönünden zayıf alkali toprakları bile tolere edebilir. Süzek olması hayati önem taşır.",
            wateringInstructions = "Doğal olarak dağ zirvelerinde susuzluğa alışkındır. Evde saksıda yetiştirirken sadece toprağı tamamen kuruduğunda, derinlemesine sulanmalıdır.",
            sunlight = "Yüksek ışık yoğunluğunu sever. Bol güneşli, bol hava sirkülasyonu olan açık pencereler veya balkonlar uygundur.",
            sowingSeason = "Tohumları kış sonunda (Şubat-Mart) iç mekanda çimlendirilip, ilkbaharda dışarıya şaşırtılmalıdır.",
            conservationStatus = "VU",
            difficultyLevel = "Orta",
            wateringFrequency = 1,
            lightType = "Güneşli"
        ),
        Plant(
            id = "antalya_cigdemi",
            name = "Antalya Çiğdemi",
            scientificName = "Crocus antalyensis",
            category = PlantCategory.ENDEMIC_PLANT,
            region = "Antalya ve Çevresi (Meşe ormanları ve açıklıklar)",
            description = "Dünyada sadece Antalya ve çevresinde çok dar bir alanda yetişen lokal endemik bir çiğdem türüdür. Kış sonunda, ocak ve mart ayları arasında narin açık leylak veya mavi-mor renkli çiçekleriyle topraktan fışkırır.",
            benefits = "Biyoçeşitlilik açısından eşsiz bir miras değerine sahiptir. Tıbbi amaçla kullanılmaz, nesli koruma altındaki endemik bir hazinedir.",
            soilRequirements = "Humuslu, gevşek ve derin orman toprağı ile yaprak çürüntüsü karışımını çok sever. Toprak hafif asidik veya nötr olmalıdır.",
            wateringInstructions = "Sonbahar ve kış aylarında (aktif büyüme dönemi) toprağı hafif nemli kalacak şekilde düzenli sulanmalı, yazın yapraklar kuruyunca sulama kesilmelidir.",
            sunlight = "Yarı gölgeli ağaç altı bölgeler, süzülmüş güneş ışığı alan balkonlar doğal yaşam alanını taklit eder.",
            sowingSeason = "Yumruları eylül - ekim aylarında 8-10 cm derinliğe ekilmelidir.",
            conservationStatus = "EN",
            difficultyLevel = "Zor",
            wateringFrequency = 2,
            lightType = "Yarı Gölge"
        ),
        Plant(
            id = "anadolu_sumagi",
            name = "Anadolu Sumağı",
            scientificName = "Rhus coriaria",
            category = PlantCategory.SPICE,
            region = "Güneydoğu Anadolu, Doğu Akdeniz ve Ege",
            description = "Çalı formunda büyüyen, kırmızı salkım meyveli, yaprak döken bir bitkidir. Bu meyveler kurutulup tuzla öğütülerek Türkiye mutfağının vazgeçilmez ekşi baharatı olan 'Sumak' elde edilir. Yabani olarak yol kenarlarında ve kurak yamaçlarda sıkça görülür.",
            benefits = "Çok güçlü anti-inflamatuar ve antioksidan etkiye sahiptir. Kan şekerini dengelemeye yardımcı olur. Yemeklere harika bir mayhoşluk, asidik bir tazelik katar.",
            soilRequirements = "Toprak seçiciliği yoktur. Kurak, kumlu, killi, besince fakir topraklarda dahi mükemmel gelişir. Erozyonu önlemek için de dikilir.",
            wateringInstructions = "Yerleştikten sonra kuraklığa muazzam dayanıklıdır. İlk yılında haftalık sulama istenirken, sonraki yıllarda sadece aşırı kurak dönemlerde sulama yapılmalıdır.",
            sunlight = "Tam güneş alan sıcak, açık alanları tercih eder.",
            sowingSeason = "Sonbahar sonu veya ilkbahar başında kök çelikleri veya tohumla dikilebilir.",
            conservationStatus = "LC",
            difficultyLevel = "Kolay",
            wateringFrequency = 1,
            lightType = "Güneşli"
        ),
        Plant(
            id = "kapadokya_sogani",
            name = "Kapadokya Soğanı (Kral Geven)",
            scientificName = "Allium cappadocicum",
            category = PlantCategory.ENDEMIC_PLANT,
            region = "İç Anadolu (Nevşehir, Kayseri jipsli tepeler)",
            description = "Kapadokya'nın tüf ve jips içeren çorak yamaçlarında yetişen, pembe yuvarlak kafa çiçekleriyle dikkat çeken endemik ve nadir bir yaban soğanı türüdür. Volkanik toprakların zorlu koşullarına mükemmel uyum sağlamıştır.",
            benefits = "Biyoçeşitlilik mirasıdır. Yabani türleri soğanlı bitkilerin gen kaynaklarının korunması açısından uluslararası öneme sahiptir.",
            soilRequirements = "Volkanik tüf veya jipsli, çok geçirgen, kireçli gevşek toprak yapısı arar. Süzek saksı karışımları şarttır.",
            wateringInstructions = "Nem konusunda çok hassastır. Sadece uyanma döneminde (ilkbahar) hafifçe sulanmalı, diğer mevsimlerde kuru bırakılmalıdır.",
            sunlight = "Kapadokya sıcağına alışık olduğundan tam yoğun güneş alan kavurucu rüzgarlı mekanları çok sever.",
            sowingSeason = "Yumrulu soğanları sonbaharda ekilmelidir.",
            conservationStatus = "CR",
            difficultyLevel = "Zor",
            wateringFrequency = 1,
            lightType = "Güneşli"
        ),
        Plant(
            id = "maras_biberi",
            name = "Maraş Biberi (Kırmızı Pul Biber)",
            scientificName = "Capsicum annuum L. (Maraş Varyetesi)",
            category = PlantCategory.SPICE,
            region = "Kahramanmaraş ve Çevresi",
            description = "Maraş biberi, bölgenin iklimi ve toprak yapısıyla kendine has aroma, acılık ve parlak kırmızı renk kazanan bir tescilli kültür baharatıdır. Toplanan taze kırmızı meyveler güneşte kurutulup saplarından ayrılır ve zeytinyağıyla hafifçe işlenerek pul biber haline getirilir.",
            benefits = "İçerdiği 'kapsaisin' sayesinde metabolizmayı hızlandırır, ağrı kesici ve hazım kolaylaştırıcıdır. C vitamini deposudur.",
            soilRequirements = "Organik maddece zengin, süzek, nemli ve derin bahçe topraklarında en yüksek verimi verir. Yanmış çiftlik gübresiyle zenginleştirilmiş tınlı toprak idealdir.",
            wateringInstructions = "Biber bitkisi suyu sever. Çiçeklenme ve meyve bağlama dönemlerinde toprağı kurutulmamalı, haftada 2-3 kez derinlemesine sulanmalıdır.",
            sunlight = "Günde 6-8 saat tam güneş alan sıcak yerleri çok sever. Gölgede kalırsa acılığı ve tadı azalır.",
            sowingSeason = "İlkbahar başlangıcında (Don tehlikesi bittikten sonra Mart - Nisan gibi fidelenerek toprakla buluşturulur).",
            conservationStatus = "LC",
            difficultyLevel = "Kolay",
            wateringFrequency = 3,
            lightType = "Güneşli"
        )
    )

    fun getPlantById(id: String): Plant? = plants.find { it.id == id }

    fun searchPlants(query: String): List<Plant> {
        if (query.isBlank()) return plants
        val q = query.trim().lowercase()
        return plants.filter {
            it.name.lowercase().contains(q) ||
                    it.scientificName.lowercase().contains(q) ||
                    it.region.lowercase().contains(q) ||
                    it.description.lowercase().contains(q)
        }
    }
}
