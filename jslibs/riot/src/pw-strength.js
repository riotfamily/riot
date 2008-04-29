var PasswordStrengthMeter = Class.create({

    ranges: [
        {name: 'weak', score: 33},
        {name: 'fair', score: 67},
        {name: 'strong', score: 100}
    ],

    labels: {
        weak: 'Weak',
        fair: 'Fair',
        strong: 'Strong'
    },

    initialize: function(input, meter) {
    	this.setInput(input);
    	this.meter = $(meter);
    },
    
    setInput: function(el) {
    	this.input = $(el);
    	this.input.onkeyup = this.onKeyUp.bind(this);
    	this.input.strengthMeter = this;
    },

    onKeyUp: function() {
        var score = this.getScore(this.input.value);
        for (var i = 0; i < this.ranges.length; i++) {
            var range = this.ranges[i];
            if (score <= range.score) {
                this.meter.update(this.labels[range.name]).className = range.name;
                return;
            }
        }
    },

    checkRepetition: function(rep, s) {
        var res = '';
        for (var i = 0; i < s.length; i++) {
            var repeated = true;
            for (var j = 0; j < rep && (i + j + rep) < s.length; j++) {
                repeated &= s.charAt(i + j) == s.charAt(i + j + rep);
            }
            if (j < rep) repeated = false;
            if (repeated) {
                i += rep - 1;
                repeated = false;
            }
            else {
                res += s.charAt(i);
            }
        }
        return res.length - s.length;
    },

    getScore: function(password) {
        var score = 0 ;
        if (password.length > 3) { 
            score += password.length * 4
            score += this.checkRepetition(1, password);
            score += this.checkRepetition(2, password);
            score += this.checkRepetition(3, password);
            score += this.checkRepetition(4, password);

            //password has 3 numbers
            if (password.match(/.*[0-9].*[0-9].*[0-9]/))  score += 5;
    
            //password has 2 sybols
            if (password.match(/(.*[!@#$%\^&*?,_~].*[!@#$%\^&*?,_~])/)) score += 5;
    
            //password has Upper and Lower chars
            if (password.match(/(?=.*[a-z]).*[A-Z]/)) score += 10;
    
            //password has number and chars
            if (password.match(/(?=.*[a-zA-Z]).*\d/)) score += 15 

            //password has number and symbol
            if (password.match(/(?=.*\d).*[!@#$%\^&*?,_~]/)) score += 15 
    
            //password has char and symbol
            if (password.match(/(?=.*[a-zA-Z]).*[!@#$%\^&*?,_~]/)) score += 15 
    
            //password is just a number or a bunch of chars
            if (password.match(/^(\d+|[a-zA-Z]+)$/)) score -= 10 
        }
        return Math.max(0, Math.min(score, 100));
    }

});
