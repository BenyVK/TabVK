<div align="center">

# TabVK

**پلاگین همه‌کاره‌ی تب‌لیست انیمیشنی برای سرورهای ماینکرافت، با پشتیبانی کامل از RankVK.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-62b47a?style=flat-square)](#نیازمندی‌ها)
[![Version](https://img.shields.io/badge/version-1.0.2-blue?style=flat-square)](#)
[![Author](https://img.shields.io/badge/author-BenyVK-orange?style=flat-square)](https://github.com/BenyVK)

[English](README.md) | **فارسی**

</div>

<div dir="rtl">

---

## درباره‌ی پلاگین

TabVK یک پلاگین سبک و کاملاً قابل تنظیم برای تب‌لیست (TAB) سرورهای مبتنی بر Paper است. با آن می‌توانید هدر و فوتر انیمیشنی، فرمت دلخواه برای لیست بازیکنان، پلیس‌هولدرهای زنده، گرادینت، طرح‌بندی جدا برای هر ورلد و نوار پینگ بسازید؛ همه‌چیز فقط از یک فایل `configvk.yaml`.

TabVK سیستم رنک مخصوص خودش را ندارد. پیشوند، پسوند، گروه و وزن گروه‌ها از **[RankVK](https://github.com/BenyVK)** خوانده می‌شود و تب‌لیست به‌صورت خودکار بر اساس وزن گروه مرتب می‌شود.

## ویژگی‌ها

- **هدر و فوتر انیمیشنی** با چند خط، فریم‌های دلخواه و سرعت قابل تنظیم
- **رنگ و گرادینت**: کدهای قدیمی (`&a`، `&l`)، هگز (`&#ff00ff`) و MiniMessage (`<gradient>`، `<rainbow>`) و حتی گرادینت متحرک با `%phase%`
- **انیمیشن‌های سفارشی** (`%anim_<name>%`): عنوان، نوار لودینگ، لینک‌های چرخشی
- **پلیس‌هولدرهای متنوع**: پینگ، نوار پینگ، TPS، تعداد آنلاین/حداکثر، ورلد، مختصات، سلامتی، گرسنگی، لول، گیم‌مود، رم، ساعت و تاریخ
- **سطوح پینگ**: رنگ و نوار سیگنال بر اساس پینگ هر بازیکن
- **ادغام با RankVK**: پیشوند، پسوند، گروه و وزن از RankVK گرفته می‌شود و لیست بر اساس وزن مرتب می‌شود (وزن بالاتر، جایگاه بالاتر)
- **هدر/فوتر مخصوص هر ورلد**: طرح‌بندی متفاوت برای ندر، اند یا هر ورلد دیگر
- **شخصی‌سازی توسط بازیکن**: بازیکنان می‌توانند پیشوند و پسوند خودشان را تنظیم کنند (با محدودیت طول و پرمیشن رنگ)
- **نیم‌تگ (Nametag)**: نمایش اختیاری پیشوند و پسوند بالای سر بازیکن
- **جملات چرخشی**: پیام‌های سرگرم‌کننده در فوتر با `%quote%`
- **پلیس‌هولدر و آیکون سفارشی**: مثل `%server_name%`، `%discord%` و `%icon_heart%`
- **پشتیبانی از PlaceholderAPI** (اختیاری): استفاده از همه‌ی پلیس‌هولدرهای PAPI در هر جا
- **پشتیبانی از منطقه‌ی زمانی**: تنظیم timezone، فرمت ساعت و فرمت تاریخ
- **پیشنهاد دستورات تمیزتر**: امکان مخفی کردن دستورات `plugin:command` از لیست پیشنهادها
- **روشن/خاموش کردن توسط بازیکن**: هر بازیکن می‌تواند هدر/فوتر خودش را خاموش کند

## نیازمندی‌ها

| مورد | جزئیات |
|---|---|
| سرور | Paper (یا فورک‌های آن)، ماینکرافت **1.21 به بالا** |
| جاوا | 21 به بالا |
| پیشنهادی | [RankVK](https://github.com/BenyVK) برای رنک و مرتب‌سازی |
| اختیاری | PlaceholderAPI، LuckPerms |

## نصب

1. آخرین نسخه‌ی `TabVK.jar` را از صفحه‌ی [Releases](https://github.com/BenyVK/TabVK/releases) دانلود کنید.
2. آن را داخل پوشه‌ی `plugins/` سرور بگذارید.
3. اگر رنک، پیشوند و مرتب‌سازی می‌خواهید، **RankVK** را هم نصب کنید.
4. سرور را اجرا کنید. فایل `plugins/TabVK/configvk.yaml` در اولین اجرا ساخته می‌شود.
5. کانفیگ را ویرایش کنید و دستور `/tabvk reload` را بزنید.

## دستورات

دستور اصلی `/tabvk` است و `/tvk` هم کار می‌کند.

| دستور | توضیح | پرمیشن |
|---|---|---|
| `/tabvk help` | نمایش لیست دستورات | ندارد |
| `/tabvk about` | اطلاعات پلاگین | ندارد |
| `/tabvk toggle` | روشن/خاموش کردن هدر و فوتر خودتان | `tabvk.toggle` |
| `/tabvk prefix <text\|reset>` | تنظیم پیشوند خودتان | `tabvk.customize.prefix` |
| `/tabvk suffix <text\|reset>` | تنظیم پسوند خودتان | `tabvk.customize.suffix` |
| `/tabvk set <player> <prefix\|suffix> <text\|reset>` | ویرایش پیشوند یا پسوند بازیکن دیگر | `tabvk.admin` |
| `/tabvk reload` | بارگذاری مجدد کانفیگ | `tabvk.admin` |

## پرمیشن‌ها

| پرمیشن | توضیح | پیش‌فرض |
|---|---|---|
| `tabvk.admin` | ریلود کانفیگ و ویرایش بازیکنان دیگر | OP |
| `tabvk.toggle` | روشن/خاموش کردن هدر و فوتر برای خود | همه |
| `tabvk.customize.prefix` | تنظیم پیشوند شخصی | OP |
| `tabvk.customize.suffix` | تنظیم پسوند شخصی | OP |
| `tabvk.color` | استفاده از رنگ و گرادینت در پیشوند/پسوند شخصی | OP |

## تنظیمات

همه‌چیز در فایل `plugins/TabVK/configvk.yaml` است. این فایل کامنت‌های زیادی دارد و شامل نمونه رنک‌های آماده برای RankVK هم هست.

</div>

```yaml
header-footer:
  enabled: true
  header:
    - "%anim_title%"
    - "%icon_arrow% &7Welcome <gradient:#55ffff:#ff55ff:%phase%><bold>%player%</bold></gradient>"
  footer:
    - "&7Ping: %ping_bars% &f%ping%ms  &8|  &7TPS: %tps_color%%tps%"
    - "&7Online: &a%online%&7/&c%max%  &8|  &7World: &e%world%"

player-list:
  enabled: true
  format: "%prefix%%name_color%%player%%suffix% &8| %ping_color%%ping%ms"
```

<div dir="rtl">

### پلیس‌هولدرهای اصلی

`%player%` `%ping%` `%ping_color%` `%ping_bars%` `%online%` `%max%` `%tps%` `%tps_color%` `%world%` `%x%` `%y%` `%z%` `%health%` `%food%` `%level%` `%gamemode%` `%ram_used%` `%ram_max%` `%time%` `%date%` `%group%` `%prefix%` `%suffix%` `%name_color%` `%quote%` `%phase%` `%anim_<name>%` `%icon_<name>%`

### شروع سریع با RankVK

</div>

```
/rank group vip create
/rank group vip setweight 50
/rank group vip setprefix &6[VIP]
/rank user <player> parent add vip
```

<div dir="rtl">

تغییرات حدود نیم‌ثانیه بعد در تب‌لیست نمایش داده می‌شود.

## نکات

- فونت پیش‌فرض ماینکرافت متن‌های راست‌به‌چپ مثل فارسی و عربی را در تب‌لیست درست نمایش نمی‌دهد، پس برای این بخش متن انگلیسی بهتر جواب می‌دهد.
- اگر پلاگین دیگری تیم‌های اسکوربورد را مدیریت می‌کند، گزینه‌ی `nametag.enabled` را روی `false` نگه دارید.

## ساخت از روی سورس

</div>

```bash
git clone https://github.com/BenyVK/TabVK.git
cd TabVK
# build with your Paper 1.21 API setup (Gradle or Maven)
```

<div dir="rtl">

## سازنده

ساخته‌شده توسط **[BenyVK](https://github.com/BenyVK)** · [www.gharri.ir](https://www.gharri.ir)

---

</div>

<div align="center">

[English](README.md) | **فارسی**

</div>
