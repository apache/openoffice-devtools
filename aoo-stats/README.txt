Instructions provided by Rob Weir on the dev mailing list.

See: https://www.mail-archive.com/dev@openoffice.apache.org/msg22689.html

Rob Weir Sun, 08 Feb 2015 11:20:05 -0800

On Sun, Feb 8, 2015 at 10:50 AM, Alexandro Colorado <j...@oooes.org> wrote:
> On Sun, Feb 8, 2015 at 10:16 AM, Rob Weir <r...@robweir.com> wrote:
>
>> On Sat, Feb 7, 2015 at 5:40 AM, Alexandro Colorado <j...@oooes.org> wrote:
>> > I notice that the stats havent been update to 2015. We are already on
>> > February and 2015 don't show up in the scale.I would expect at least a 5%
>> > of the graph to belong to 2015.
>> >
>>
>> I've been updating the stats occasionally, using the scripts here:
>>
>> https://svn.apache.org/repos/asf/openoffice/devtools/aoo-stats/
>>
>> Only the querying from SourceForge is automated.  The update of the
>> CSV file that produces the chart is still manual.
>>
>> If you want to give it a try, I'm happy to answer any questions.
>> Otherwise it is easy for me to update this.
>>
>
> Well I have two, one was for the final URL that the script needed from
> Sourceforge?
> The other question is related, but as I look on SF API seems this is for a
> single file download, and I wonder how to concurrently do all of the files
> from the release.
>



The detail-by-day.py script is the one you want.   You'll also need to
"all.lst" data file from the same directory.  It is a list of all the
program file downloads on SF for AOO, for all releases.

You run like:

python detail-by-day.py all.lst start-date end-date

for example:

python detail-by-day.py all.lst 2015-01-01 2015-02-01 >out.csv

You can then load the CSV file into Calc and do calculations from there.

Regards,

-Rob



> However, I tried this with the detail-by-day.py script which required the
> SF URL, however if this script you showed me dont need any arguments (URL
> or date limits) I guess it would be fine.
>
> I tested the get-aoo-stats.py but also ask me for a list of URLs (I assume
> these are for each file from the release). If you can provide a sample dump
> for the current release, it would be good enough for me.
>
> get-aoo-stats.py
> syntax:  python get-aoo-stats.py <urls.lst> <iso-date> [<iso-date>]
> where <file.list> is a list of files URL's to gather stats on,
> and <iso-date> is a date of interest, in YYYY-MM-DD format.
> If two dates are given this expresses a range of dates.
>
>
>
>
>>
>> Regards,
>>
>> -Rob
>>
>>
>> > If there is a way to manually update these stats, I could try to do the
>> > work.
>> >
>> > --
>> > Alexandro Colorado
>> > Apache OpenOffice Contributor
>> > 882C 4389 3C27 E8DF 41B9  5C4C 1DB7 9D1C 7F4C 2614
>>
>> ---------------------------------------------------------------------
>> To unsubscribe, e-mail: dev-unsubscr...@openoffice.apache.org
>> For additional commands, e-mail: dev-h...@openoffice.apache.org
