// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.datetime;

import jodd.util.HashCode;
import jodd.util.StringBand;

import java.io.Serializable;

/**
 * Generic date time stamp just stores and holds date and time information.
 * This class does not contain any date/time logic, neither guarantees
 * that date is valid.
 *
 * @see JDateTime
 * @see JulianDateStamp
 */
public class DateTimeStamp implements Comparable, Serializable, Cloneable {

    /**
     * Default empty constructor.
     */
    public DateTimeStamp() {
    }

    /**
     * Constructor that sets date and time.
     */
    public DateTimeStamp(final int year, final int month, final int day, final int hour, final int minute, final int second, final int millisecond) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
    }

    /**
     * Constructor that sets just date. Time is set to zeros.
     */
    public DateTimeStamp(final int year, final int month, final int day) {
        this(year, month, day, 0, 0, 0, 0);
    }

    /**
     * Year.
     */
    public int year;

    /**
     * Month, range: [1 - 12]
     */
    public int month = 1;

    /**
     * Day, range: [1 - 31]
     */
    public int day = 1;

    /**
     * Hour, range: [0 - 23]
     */
    public int hour;

    /**
     * Minute, range [0 - 59]
     */
    public int minute;

    /**
     * Second, range: [0 - 59]
     */
    public int second;

    /**
     * Millisecond, range: [0 - 1000]
     */
    public int millisecond;


    // ---------------------------------------------------------------- get/set

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(final int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(final int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(final int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(final int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(final int second) {
        this.second = second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(final int millisecond) {
        this.millisecond = millisecond;
    }

    // ---------------------------------------------------------------- compare

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    @Override
    public int compareTo(final Object o) {
        DateTimeStamp dts = (DateTimeStamp) o;

        int date1 = year * 10000 + month * 100 + day;
        int date2 = dts.year * 10000 + dts.month * 100 + dts.day;

        if (date1 < date2) {
            return -1;
        }
        if (date1 > date2) {
            return 1;
        }

        date1 = (hour * 10000000) + (minute * 100000) + (second * 1000) + millisecond;
        date2 = (dts.hour * 10000000) + (dts.minute * 100000) + (dts.second * 1000) + dts.millisecond;

        if (date1 < date2) {
            return -1;
        }
        if (date1 > date2) {
            return 1;
        }
        return 0;
    }

    /**
     * Compares just date component of two date time stamps.
     */
    public int compareDateTo(final Object o) {
        DateTimeStamp dts = (DateTimeStamp) o;

        int date1 = year * 10000 + month * 100 + day;
        int date2 = dts.year * 10000 + dts.month * 100 + dts.day;

        if (date1 < date2) {
            return -1;
        }
        if (date1 > date2) {
            return 1;
        }
        return 0;
    }


    // ---------------------------------------------------------------- toString

    /**
     * Simple to string conversion.
     *
     * @return date/time string in 'y-m-d h:m:m.s' format
     */
    @Override
    public String toString() {
        return this.toString("-", ":", ".", " ");
    }

    /**
     * Simple to string conversion using provided character.
     * example : y(dateConnector)m(dateConnector)d(midConnector)h(timeConnector)m(timeConnector)m(millisecondConnector)s
     *
     * @param dateConnector        connect data's connector
     * @param timeConnector        connect time's connector
     * @param millisecondConnector connect time and millisecond connector
     * @param midConnector         connect date and time connector
     * @return date/time string using provided character format
     */
    public String toString(final String dateConnector, final String timeConnector, final String millisecondConnector,
                           final String midConnector) {

        return new StringBand().append(year).append(dateConnector).append(month)
                .append(dateConnector).append(day).append(midConnector).append(hour).append(timeConnector)
                .append(minute).append(timeConnector).append(second).append(millisecondConnector)
                .append(millisecond).toString();
    }

    // ---------------------------------------------------------------- equals & hashCode

    @Override
    public boolean equals(final Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        if (this == object) {
            return true;
        }
        DateTimeStamp stamp = (DateTimeStamp) object;
        return  (stamp.year == this.year) &&
                (stamp.month == this.month) &&
                (stamp.day == this.day) &&
                (stamp.hour == this.hour) &&
                (stamp.minute == this.minute) &&
                (stamp.second == this.second) &&
                (stamp.millisecond == this.millisecond);
    }

    @Override
    public int hashCode() {
        return HashCode.create()
                .hash(year)
                .hash(month)
                .hash(day)
                .hash(hour)
                .hash(minute)
                .hash(second)
                .hash(millisecond)
                .get();
    }

// ---------------------------------------------------------------- clone


    @Override
    public DateTimeStamp clone() {
        DateTimeStamp dts = new DateTimeStamp();
        dts.year = this.year;
        dts.month = this.month;
        dts.day = this.day;
        dts.hour = this.hour;
        dts.minute = this.minute;
        dts.second = this.second;
        dts.millisecond = this.millisecond;
        return dts;
    }

    // ---------------------------------------------------------------- equals

    public boolean isEqualDate(final DateTimeStamp date) {
        return date.day == this.day
                && date.month == this.month
                && date.year == this.year;
    }

    public boolean isEqualTime(final DateTimeStamp time) {
        return time.hour == this.hour
                && time.minute == this.minute
                && time.second == this.second
                && time.millisecond == this.millisecond;
    }


}