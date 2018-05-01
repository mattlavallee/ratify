import * as express from 'express';
import {config as dotenvConfig} from 'dotenv';
import * as bodyParser from 'body-parser';
import registerHomeRoutes from './routes/home';
import registerGroupRoutes from './routes/group';

dotenvConfig();

const app = express();

app.set('port', process.env.PORT || 8000);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const routeRegistration: Function[] = [
  registerHomeRoutes,
  registerGroupRoutes,
];

for(let i = 0; i < routeRegistration.length; i++) {
  routeRegistration[i](app);
}

app.listen(app.get('port'), () => {
  console.log(('App is running at http://localhost:%d in %s mode'),
    app.get('port'), app.get('env'));
  console.log('Press CTRL-C to stop\n');
});

export default app;
