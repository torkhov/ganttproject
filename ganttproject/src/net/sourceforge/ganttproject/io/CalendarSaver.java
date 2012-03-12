/*
GanttProject is an opensource project management tool.
Copyright (C) 2011 GanttProject Team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.sourceforge.ganttproject.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.transform.sax.TransformerHandler;

import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.calendar.GPCalendar;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class CalendarSaver extends SaverBase {
  private SimpleDateFormat myShortFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);

  private Calendar myCalendar = GregorianCalendar.getInstance(Locale.ENGLISH);

  void save(IGanttProject project, TransformerHandler handler) throws SAXException {
    AttributesImpl attrs = new AttributesImpl();
    startElement("calendars", attrs, handler);
    startElement("day-types", attrs, handler);

    addAttribute("id", "0", attrs);
    emptyElement("day-type", attrs, handler);
    addAttribute("id", "1", attrs);
    emptyElement("day-type", attrs, handler);

    addAttribute("id", "1", attrs);
    addAttribute("name", "default", attrs);
    startElement("calendar", attrs, handler);
    for (int i = 1; i <= 7; i++) {
      boolean holiday = project.getActiveCalendar().getWeekDayType(i) == GPCalendar.DayType.WEEKEND;
      addAttribute(getShortDayName(i), holiday ? "1" : "0", attrs);
    }
    emptyElement("default-week", attrs, handler);
    addAttribute("value", project.getActiveCalendar().getOnlyShowWeekends(), attrs);
    emptyElement("only-show-weekends", attrs, handler);
    emptyElement("overriden-day-types", attrs, handler);
    emptyElement("days", attrs, handler);
    endElement("calendar", handler);

    endElement("day-types", handler);
    for (Date d : project.getActiveCalendar().getPublicHolidays()) {
      if (d.getYear() == 1 - 1900) {
        addAttribute("year", "", attrs);
      } else {
        addAttribute("year", String.valueOf(d.getYear() + 1900), attrs);
      }
      addAttribute("month", String.valueOf(d.getMonth() + 1), attrs);
      addAttribute("date", String.valueOf(d.getDate()), attrs);
      emptyElement("date", attrs, handler);
    }

    endElement("calendars", handler);
  }

  private String getShortDayName(int i) {
    myCalendar.set(Calendar.DAY_OF_WEEK, i);
    return myShortFormat.format(myCalendar.getTime()).toLowerCase();
  }
}
